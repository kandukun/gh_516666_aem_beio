package com.bms.beio.workflow.models;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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

@Component(immediate=true,service=WorkflowProcess.class, property = {"process.label=BEIO process to Update Date"})
public class PushNotificationDateUpdateProcess implements WorkflowProcess{

	private static final Logger LOG = LoggerFactory.getLogger(PushNotificationDateUpdateProcess.class);

	@Reference
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;

	/**
	 * This method will get the formType from current resource and make function call to construct a list of fragments based on formType.	
	 * @param workItem
	 * @param workflowSession
	 * @param metadataMap
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metadataMap)
			throws WorkflowException {
		LOG.debug("::::::::::Entering the execute method of PushNotificationDateUpdateProcess class::::::::::");
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			ResourceResolver resolver = autoClosingResolver.getResurceResolver();
			String path = workItem.getWorkflowData().getPayload().toString();
			Resource currentResource = resolver.getResource(path.concat(BEIOConstants.JCR_CONTENT));
			String lastPublishedDate = StringUtils.EMPTY;
			Optional<Resource> currentResourceNullChk = Optional.of(currentResource);
			Optional<Boolean>resourceCheck =  currentResourceNullChk.map(Resource::getValueMap).map(vm->vm.get(BEIOConstants.SLING_RESOURCE_TYPE,String.class).equalsIgnoreCase(BEIOConstants.GENERIC_LISTING_PAGE));
			if(resourceCheck.isPresent() && resourceCheck.get()) {
				ValueMap previewPageValueMap = currentResourceNullChk.get().getValueMap();
				if(previewPageValueMap.containsKey(BEIOConstants.CQ_LAST_REPLICATED)&& previewPageValueMap.containsKey(BEIOConstants.LAST_REPLICATION_ACTION)) {
					if(previewPageValueMap.get(BEIOConstants.LAST_REPLICATION_ACTION).equals(BEIOConstants.ACTIVATE)){
						lastPublishedDate = previewPageValueMap.get(BEIOConstants.CQ_LAST_REPLICATED,String.class);
						if(!previewPageValueMap.containsKey(BEIOConstants.LAST_NOTIFICATION_SENT_DATE)){
							ModifiableValueMap map = currentResource.adaptTo(ModifiableValueMap.class);
							if(null!=map) {
								map.put(BEIOConstants.LAST_NOTIFICATION_SENT_DATE, lastPublishedDate);	
								currentResource.getResourceResolver().commit();
							}
						}
					}
				}
			}
		} catch (IllegalArgumentException | LoginException e){
			LOG.error(" ::::::: Exception in execute Method of PushNotificationDateUpdateProcess class ::::::: " , e);
		} catch(Exception e){
			LOG.error(":::: Broad Exception  in execute method of PushNotificationDateUpdateProcess class ::::: ",e);
		}
		LOG.debug("::::::::Exiting PushNotificationDateUpdateProcess #execute method::::::::::::");
	}
}
