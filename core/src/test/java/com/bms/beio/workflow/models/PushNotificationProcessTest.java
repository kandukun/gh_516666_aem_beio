package com.bms.beio.workflow.models;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.service.AWSLambdaConnectionService;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.beio.utils.CommonUtils;
import com.bms.beio.utils.ContentFragmentHelper;
import com.bms.beio.utils.QueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PushNotificationProcess.class,CommonUtils.class,QueryBuilder.class,ContentFragmentHelper.class})
public class PushNotificationProcessTest {
	
	@InjectMocks
	PushNotificationProcess classObj;
	
	@Mock
	AWSLambdaConnectionService awsService;
	
	@Mock 
	WorkflowSession workflowSession;
	
	@Mock
	BEIOAutoClosingResourceResolver autoCloseResolver;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory resolverFactory;
	
	@Mock
	WorkItem workItem;
	
	@Mock
	ValueMap valueMap;
	
	@Mock
	WorkflowData workflowData;
	
	@Mock
	MetaDataMap metaDataMap;
	
	@Mock
	Resource resource;
	
	@Mock 
	ResourceResolver resolver;

	@Mock
	ObjectMapper mapper;
	
	@Mock
	ObjectNode node;
	
	@Mock
	GenericListingComponentModel genericListingComponentModel;
	
	@Mock
	ContentFragmentHelper<ProductPromoModel> productContentFragmentHelper;
	
	@Mock
	ContentFragmentHelper<ProductNonPromoModel> nonproductContentFragmentHelper;
	
	@Mock
	ProductPromoModel productpromomodel;
	
	@Mock
	ProductNonPromoModel nonproductpromomodel;
	
	@Mock
	ModifiableValueMap modifiableValueMap;
	
	private String productsPayloadPath = "/content/beio/us/en_us/home/products";
	String lastReplicatedDate = "2018-11-26T20:41:40.428+05:30";
	
	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(CommonUtils.class);
		PowerMockito.mockStatic(QueryBuilder.class);
		PowerMockito.mockStatic(ContentFragmentHelper.class);
		when(resolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoCloseResolver);
		when(autoCloseResolver.getResurceResolver()).thenReturn(resolver);
		when(mapper.createObjectNode()).thenReturn(node);
	}
	
	
	@Test
	public void testExecute() throws Exception {
		when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
		when(workItem.getWorkflowData()).thenReturn(workflowData);
		when(workflowData.getPayload()).thenReturn(productsPayloadPath);
		when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);
		when(resolver.getResource(productsPayloadPath+BEIOConstants.JCR_CONTENT)).thenReturn(resource);
		when(resource.getValueMap()).thenReturn(valueMap);
		when(valueMap.containsKey(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)).thenReturn(true);
		when(valueMap.get(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)).thenReturn("lastNotificationSentDate");
		List<GenericListingComponentModel> genericListingComponents = new ArrayList<GenericListingComponentModel>();
		genericListingComponents.add(genericListingComponentModel);
		PowerMockito.when(CommonUtils.getGenericListingComponentModel(resource)).thenReturn(genericListingComponents);
		when(metaDataMap.get(BEIOConstants.LAST_PUBLISHED_DATE,String.class)).thenReturn(lastReplicatedDate);
		
		when(genericListingComponentModel.getFormtype()).thenReturn("all");
		classObj.execute(workItem, workflowSession, metaDataMap);
		
		when(genericListingComponentModel.getFormtype()).thenReturn("products");
		
		String promosourcepath="/content/dam/beio/ar/es_ar/promo";
		when(genericListingComponentModel.getPromosourcepath()).thenReturn(promosourcepath);
		PowerMockito.when(QueryBuilder.buildQueryToFetchFragments(promosourcepath, BEIOConstants.PRODUCTS_PROMO_MODEL, lastReplicatedDate)).thenReturn("promoProductQueryString");
		PowerMockito.whenNew(ContentFragmentHelper.class).withAnyArguments().thenReturn(productContentFragmentHelper);
		List<ProductPromoModel> list = Mockito.mock(ArrayList.class);
		list.add(productpromomodel);
		when(productContentFragmentHelper.getFragmentList(resolverFactory, "promoProductQueryString", ProductPromoModel.class, BEIOConstants.BEIO_ADMIN)).thenReturn(list);
		classObj.execute(workItem, workflowSession, metaDataMap);
		
		Map<String,String> obj = new HashMap<String,String>();
		obj.put(BEIOConstants.APP_NAME, StringUtils.upperCase("beio"));
		PowerMockito.when(CommonUtils.getDetails("/content/beio/us/en_us/home/products")).thenReturn(obj);
		String nonpromosourcepath="/content/dam/beio/ar/es_ar/non-promo";
		when(genericListingComponentModel.getNonpromosourcepath()).thenReturn(nonpromosourcepath);
		PowerMockito.when(QueryBuilder.buildQueryToFetchFragments(nonpromosourcepath, BEIOConstants.PRODUCTS_NON_PROMO_MODEL, lastReplicatedDate)).thenReturn("nonPromoProductQueryString");
		PowerMockito.whenNew(ContentFragmentHelper.class).withAnyArguments().thenReturn(nonproductContentFragmentHelper);
		List<ProductNonPromoModel> nonpromolist = Mockito.mock(ArrayList.class);
		nonpromolist.add(nonproductpromomodel);
		when(nonproductContentFragmentHelper.getFragmentList(resolverFactory, "nonPromoProductQueryString", ProductNonPromoModel.class, BEIOConstants.BEIO_ADMIN)).thenReturn(nonpromolist);
		classObj.execute(workItem, workflowSession, metaDataMap);
		
		when(genericListingComponentModel.getFormtype()).thenReturn("all");
		classObj.execute(workItem, workflowSession, metaDataMap);
		
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObj=Mockito.mock(JSONObject.class);
		when(jsonObj.get("role")).thenReturn("Physician,Nurse,Other");
		jsonArray.put(jsonObj);
		classObj.formNotificationDetailJSON("products", "promo", jsonArray, "/content/beio/us/en_us/home/products",jsonArray);
		classObj.formNotificationDetailJSON("products", "non-promo", jsonArray, "/content/beio/us/en_us/home/products",jsonArray);
		classObj.formNotificationDetailJSON("all", "non-promo", jsonArray, "/content/beio/us/en_us/home/products",jsonArray);
		jsonArray.put(productpromomodel);
		classObj.formNotificationDetailJSON("products", "promo", jsonArray, "/content/beio/us/en_us/home/products",jsonArray);
		ObjectMapper map=new ObjectMapper();
		classObj.getJsonDetails("products", "/content/beio/us/en_us/home/products", node, map,jsonArray);
	}
	
	@Test
	public void testSetNotificationSentDate()
	{
		when(resource.getValueMap()).thenReturn(valueMap);
		when(valueMap.containsKey(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)).thenReturn(true);
		when(valueMap.get(BEIOConstants.CQ_LAST_REPLICATED, String.class)).thenReturn("2018-11-26T20:41:40.428+05:30");
		when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);
		when(resource.getResourceResolver()).thenReturn(resolver);
		classObj.setNotificationSentDate(resource, "500");
	}
	
	@Test
	public void testCatchBlock() throws IllegalArgumentException, LoginException, WorkflowException
	{
		when(resolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		classObj.execute(workItem, workflowSession, metaDataMap);
	}
}
