package com.bms.beio.workflow.models;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.jcr.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.service.AWSLambdaConnectionService;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.beio.utils.CommonUtils;
import com.bms.beio.utils.ContentFragmentHelper;
import com.bms.beio.utils.QueryBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author rupali.gurram
 * 
 * This is a custom process step, which gets the list of content fragments to be sent as notification details to Lambda Service. 
 * 
 * **/
@Component(immediate=true,service=WorkflowProcess.class, property = {"process.label=BEIO process to Send Push Notification"})
public class PushNotificationProcess implements WorkflowProcess{

	private static final Logger LOG = LoggerFactory.getLogger(PushNotificationProcess.class);

	@Reference
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Reference
	AWSLambdaConnectionService awsService;

	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		LOG.debug("::::::: Entering execute Method of PushNotificationProcess Class ::::::: ");
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			JSONArray notificationDetailsArray = new JSONArray();
			String serviceResponse = StringUtils.EMPTY;
			String lastNotificationSentDate = StringUtils.EMPTY;
			ResourceResolver resolver = autoClosingResolver.getResurceResolver();
			String path = workItem.getWorkflowData().getPayload().toString();
			Resource currentResource = resolver.getResource(path.concat(BEIOConstants.JCR_CONTENT));
			Optional<Resource> currentResourceNullCheck = Optional.ofNullable(currentResource);
			ValueMap properties = currentResourceNullCheck.map(Resource::getValueMap).get();
			
			if(properties.containsKey(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)){
				lastNotificationSentDate = properties.get(BEIOConstants.LAST_NOTIFICATION_SENT_DATE).toString();
			}
			
			List<GenericListingComponentModel> listingComponents = CommonUtils.getGenericListingComponentModel(currentResource);
			/** This block constructs either a Product or All products based on the input.
			 * A Query is constructed based on the input parameters and the list of content fragments is constructed and
			 * filtered to match the user profile data.*/
			if(null!=listingComponents && listingComponents.size()>0) {
				for(GenericListingComponentModel model : listingComponents) {
					if(StringUtils.isNotBlank(model.getFormtype())) {
						switch(model.getFormtype()) {
						case BEIOConstants.ALL : 
							serviceResponse = constructAllList(model, autoClosingResourceResolverFactory, path,lastNotificationSentDate,notificationDetailsArray);
							break;
						case BEIOConstants.PRODUCTS : 
							serviceResponse = constructProductsList(model, autoClosingResourceResolverFactory, path,lastNotificationSentDate,notificationDetailsArray);
							break;
						default: break;
						}
					}
				}
			}
			if(!StringUtils.equalsIgnoreCase(serviceResponse, "200")) {
				workflowSession.terminateWorkflow(workItem.getWorkflow());
				LOG.info(" :::::: Push Notification Workflow aborted due to an exception in calling AWS Lambda Service :::::: ");
			}else {
				setNotificationSentDate(currentResource, serviceResponse);
			}
		}catch(IllegalArgumentException | LoginException e) {
			LOG.error(" ::::::: Exception occured execute Method of PushNotificationProcess Class ::::::: " , e);
		}catch(Exception exception){
			LOG.error("::::::  Exception  ::::: ", exception);
		}
		LOG.debug("::::::: Exiting execute method of PushNotificationProcess class ::::::: ");
	}

	/**
	 * This method construct list of content fragments (of type promo and non--promo products) which are published after the preview page.
	 * @param genericListingModel
	 * @param autoClosingResourceResolverFactory
	 * @param jsonResponse
	 * @param mapper
	 * @param previewPagePath
	 */
	private String constructProductsList(GenericListingComponentModel genericListingModel, BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory, String previewPagePath,String syncDate, JSONArray notificationDetailsArray) {
		LOG.debug(" ::::::: Entering constructProductsList Method of PushNotificationProcess Class ::::::: ");
		JSONObject responseObject = new JSONObject();
		String awsResponse = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonResponse =  mapper.createObjectNode();
		try {
			String promoSourcePath = genericListingModel.getPromosourcepath();
			String nonPromoSourcePath = genericListingModel.getNonpromosourcepath();
			if(StringUtils.isNotBlank(promoSourcePath)){
				String promoProductQueryString = QueryBuilder.buildQueryToFetchFragments(promoSourcePath, BEIOConstants.PRODUCTS_PROMO_MODEL, syncDate);
				LOG.debug(" ::::::: Query for promo ::::::: " + promoProductQueryString);
				ContentFragmentHelper<ProductPromoModel> productPromo = new ContentFragmentHelper<>();
				List<ProductPromoModel> productPromoList = productPromo.getFragmentList(autoClosingResourceResolverFactory, promoProductQueryString, ProductPromoModel.class,BEIOConstants.BEIO_ADMIN);
				LOG.info(" ::::::: promo list ::::::: " + productPromoList);
				jsonResponse.set(BEIOConstants.PROMO_PRODUCTS, mapper.valueToTree(productPromoList));
			}
			if(StringUtils.isNotBlank(nonPromoSourcePath)) {
				String nonPromoProductQueryString = QueryBuilder.buildQueryToFetchFragments(nonPromoSourcePath, BEIOConstants.PRODUCTS_NON_PROMO_MODEL, syncDate);
				LOG.debug(" ::::::: Query for non-promo ::::::: " + nonPromoProductQueryString);
				ContentFragmentHelper<ProductNonPromoModel> productNonPromo = new ContentFragmentHelper<>();
				List<ProductNonPromoModel> productNonPromoList = productNonPromo.getFragmentList(autoClosingResourceResolverFactory, nonPromoProductQueryString, ProductNonPromoModel.class, BEIOConstants.BEIO_ADMIN);
				jsonResponse.set(BEIOConstants.NON_PROMO_PRODUCTS, mapper.valueToTree(productNonPromoList));
			}
			LOG.debug(" ::::::: JSON response formed: " + mapper.writeValueAsString(jsonResponse));
			getJsonDetails(BEIOConstants.PRODUCTS,previewPagePath,jsonResponse,mapper,notificationDetailsArray);
			responseObject.put(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.APP_NAME), notificationDetailsArray);
			LOG.info(" ::::::: Fragments json object: " + responseObject);
			if(!responseObject.isNull(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.APP_NAME))){
				awsResponse = awsService.sendPushNotificationDetails(previewPagePath, responseObject);
			}
			LOG.info(" ::::::: Push Notification Response in constructProductsList Method of PushNotificationProcess Class  :::::::" + awsResponse);
		}catch (JSONException e) {
			LOG.error(" ::::::: JSONException in constructProductsList Method of PushNotificationProcess Class::::::: " ,e);
		} catch (JsonProcessingException e) {
			LOG.info(" ::::::: JsonProcessingException in constructProductsList Method of PushNotificationProcess Class::::::: " ,e);
		}
		LOG.debug(" ::::::: Exiting constructProductsList Method of PushNotificationProcess Class ::::::: ");
		return awsResponse;
	}
	
	/**
	 * This method construct list of content fragments (of type BEIO promo and non--promo) which are published after the preview page.
	 * @param genericListingModel
	 * @param autoClosingResourceResolverFactory
	 * @param jsonResponse
	 * @param mapper
	 * @param previewPagePath
	 */
	private String constructAllList(GenericListingComponentModel genericListingModel, BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory, String previewPagePath,String syncDate, JSONArray notificationDetailsArray) {
		LOG.debug(" ::::::: Entering constructAllList Method of PushNotificationProcess Class ::::::: ");
		JSONObject responseObject = new JSONObject();
		String awsResponse = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonResponse =  mapper.createObjectNode();
		try {
			String promosourcepath = genericListingModel.getPromosourcepath();
			String nonPromoSourcePath = genericListingModel.getNonpromosourcepath();
			if(StringUtils.isNotBlank(promosourcepath)) {
				ContentFragmentHelper<BEIOPromoModel> beioPromo = new ContentFragmentHelper<>();
				String beioPromoQueryString = QueryBuilder.buildQueryToFetchFragments(promosourcepath, BEIOConstants.BEIO_PROMO_MODEL, syncDate);
				List<BEIOPromoModel> beioPromoList = beioPromo.getFragmentList(autoClosingResourceResolverFactory, beioPromoQueryString, BEIOPromoModel.class, BEIOConstants.BEIO_ADMIN);
				jsonResponse.set(BEIOConstants.BEIO_PROMO_PRODUCTS, mapper.valueToTree(beioPromoList));
			}

			if(StringUtils.isNotBlank(nonPromoSourcePath)) {
				ContentFragmentHelper<BEIONonPromoModel> beioNonPromo = new ContentFragmentHelper<>();
				String beioNonPromoQueryString = QueryBuilder.buildQueryToFetchFragments(nonPromoSourcePath, BEIOConstants.BEIO_NON_PROMO_MODEL, syncDate);
				List<BEIONonPromoModel> beioNonPromoList = beioNonPromo.getFragmentList(autoClosingResourceResolverFactory, beioNonPromoQueryString, BEIONonPromoModel.class, BEIOConstants.BEIO_ADMIN);
				jsonResponse.set(BEIOConstants.BEIO_NON_PROMO_PRODUCTS, mapper.valueToTree(beioNonPromoList));

			}
			getJsonDetails(BEIOConstants.ALL, previewPagePath, jsonResponse, mapper,notificationDetailsArray);
			responseObject.put(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.APP_NAME), notificationDetailsArray);
			LOG.info(" ::::::::::::: Fragments json object :::::::::::::::: " + responseObject);
			if(!responseObject.isNull(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.APP_NAME))){
				awsResponse = awsService.sendPushNotificationDetails(previewPagePath, responseObject);
			}
			LOG.info("Service response: " + awsResponse);
		} catch (JSONException e) {
			LOG.error(" ::::::: JSONException in constructAllList Method of PushNotificationProcess Class ::::::: ",e);
		}
		LOG.debug(" ::::::: Exiting constructAllList Method of PushNotificationProcess Class  ::::::: ");
		return awsResponse;
	}
	
	/**
	 * @param formType
	 * @param previewPagePath
	 * @param jsonResponse
	 * @param mapper
	 * 
	 * This method iterate over the JSON array and pass its value to formJSON method to form the notification detail JSON.
	 */
	
	@SuppressWarnings("unchecked")
	public void getJsonDetails(String formType, String previewPagePath, ObjectNode jsonresponse, ObjectMapper mapper, JSONArray notificationDetailsArray){	
		LOG.debug(" ::::::: Entering getJsonDetails Method of PushNotificationProcess Class ::::::: ");
		try {
			String response = mapper.writeValueAsString(jsonresponse);
			JSONObject obj = new JSONObject(response);
			Iterator<String> keysItr = obj.keys();
			while (keysItr.hasNext()) {
				String key = keysItr.next();
				JSONArray json = obj.getJSONArray(key);
				String beioType = StringUtils.EMPTY;
				if(key.contains(BEIOConstants.NON_PROMO_PRODUCTS)){
					beioType = BEIOConstants.NON_PROMO;
				}
				else{
					beioType = BEIOConstants.PROMO;
				}
				formNotificationDetailJSON(formType, beioType, json,previewPagePath,notificationDetailsArray);
			}

		} catch (JsonProcessingException e) {
			LOG.error(" ::::::: JsonProcessingException  in getJsonDetails Method of PushNotificationProcess Class:::::::::: ",e);
		} catch (JSONException e) {
			LOG.error(" ::::::: JSONException in getJsonDetails Method of PushNotificationProcess Class :::::::: ",e);
		}
		LOG.debug(" ::::::: Exiting getJsonDetails Method of PushNotificationProcess Class ::::::: ");
	}

	/**
	 * This method filters the required properties to be sent as notification details from each JSONObject in the array and put those in responseJson.
	 * @param formType
	 * @param beioType
	 * @param jsonArray
	 * @param previewPagePath
	 */
	public void formNotificationDetailJSON(String formType, String beioType, JSONArray jsonArray, String previewPagePath, JSONArray notificationDetailsArray){
		LOG.debug(" ::::::: Entering formNotificationDetailJSON Method of PushNotificationProcess Class ::::::: ");
		try{
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = new JSONObject();
				JSONObject obj = jsonArray.getJSONObject(i);
				jsonObject.put(BEIOConstants.BEIO_TYPE, beioType);
				jsonObject.put(BEIOConstants.SUB_TITLE, obj.get(BEIOConstants.SUB_TITLE));
				jsonObject.put(BEIOConstants.PUBLISH_DATE, obj.get(BEIOConstants.PUBLISH_DATE));
				jsonObject.put(BEIOConstants.MESSAGE_TYPE, BEIOConstants.ONE);
				jsonObject.put(BEIOConstants.CFUNIQUE_ID, obj.get(BEIOConstants.CFUNIQUE_ID));
				jsonObject.put(BEIOConstants.COUNTRY, StringUtils.lowerCase(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.COUNTRY)));
				jsonObject.put(BEIOConstants.LANGUAGE, StringUtils.lowerCase(CommonUtils.getDetails(previewPagePath).get(BEIOConstants.LANGUAGE)));
				jsonObject.put(BEIOConstants.AUDIENCE, StringUtils.lowerCase(obj.get(BEIOConstants.ROLE).toString().replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\\\"", "")));
				if(formType.equals(BEIOConstants.PRODUCTS)){
					jsonObject.put(BEIOConstants.SECTION, BEIOConstants.PRODUCTS);
					if(beioType.equals(BEIOConstants.PROMO)){
						jsonObject.put(BEIOConstants.TITLE, obj.get(BEIOConstants.BRAND_NAME));
					}
					else{
						jsonObject.put(BEIOConstants.TITLE, obj.get(BEIOConstants.COMPOUND_NAME));
					}
				}
				if(formType.equals(BEIOConstants.ALL)){
					jsonObject.put(BEIOConstants.SECTION, obj.get(BEIOConstants.TYPE));
					jsonObject.put(BEIOConstants.TITLE, obj.get(BEIOConstants.TITLE));
				}
				notificationDetailsArray.put(jsonObject);
			}

		}catch(JSONException e){
			LOG.error("::::::: JSONException while constructing the JSON Object in formNotificationDetailJSON Method of PushNotificationProcess Class :::::::: ",e);
		}
		LOG.debug(" ::::::: Exiting formNotificationDetailJSON Method of PushNotificationProcess Class  ::::::: ");
	}
	
	/**
	 * @param resource
	 * @param responseCode
	 * This will save last notification sent date(Current date) on node
	 */
	public void setNotificationSentDate(Resource resource, String responseCode){
		LOG.debug(" ::::::: Entering setNotificationSentDate Method of PushNotificationProcess Class ::::::: ");
		try{
			if(StringUtils.isNotBlank(responseCode)){
				if(StringUtils.equalsIgnoreCase(responseCode, "200")){
					Optional<Resource> resourceNullChk = Optional.of(resource);
					if(resourceNullChk.map(Resource::getValueMap).get().containsKey(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)){
						ModifiableValueMap map = resourceNullChk.get().adaptTo(ModifiableValueMap.class);
						if(null!=map) {
							map.put(BEIOConstants.LAST_NOTIFICATION_SENT_DATE, resourceNullChk.map(Resource::getValueMap).get().get(BEIOConstants.CQ_LAST_REPLICATED,String.class));	
							resourceNullChk.get().getResourceResolver().commit();
						}
					}
				}
			}
		}catch(PersistenceException pe){
			LOG.error(":::::::: Exception in saving Last Notification Sent Date:::::: ",pe);
		}
		LOG.debug(" ::::::: Exiting setNotificationSentDate Method of PushNotificationProcess Class  ::::::: ");
	}
	
}
