package com.bms.beio.workflow.models;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;

@RunWith(PowerMockRunner.class)
public class PushNotificationDateUpdateProcessTest {

	@InjectMocks
	PushNotificationDateUpdateProcess classObj;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResolver;
	
	@Mock 
	ResourceResolver resolver;
	
	@Mock 
	WorkflowSession workflowSession;
	
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
	Optional<Boolean>resourceCheck;
	
	@Mock
	ModifiableValueMap map;
	
	private String payloadPath = "/content/beio/us/en_us/home/products";
	
	@Before
	public void setUp() throws Exception {
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResolver);
		when(autoClosingResolver.getResurceResolver()).thenReturn(resolver);
	}
	@Test
	public void testExecute() throws WorkflowException {
		when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
		when(workItem.getWorkflowData()).thenReturn(workflowData);
		when(workflowData.getPayload()).thenReturn(payloadPath);
		when(resolver.getResource(payloadPath+BEIOConstants.JCR_CONTENT)).thenReturn(resource);
		when(resource.getValueMap()).thenReturn(valueMap);
		when(valueMap.containsKey(BEIOConstants.SLING_RESOURCE_TYPE)).thenReturn(true);
		when(valueMap.get(BEIOConstants.SLING_RESOURCE_TYPE,String.class)).thenReturn("beio-mobile-app/components/page/beio-generic-listing-page");
		when(valueMap.containsKey(BEIOConstants.CQ_LAST_REPLICATED)).thenReturn(true);
		when(valueMap.containsKey(BEIOConstants.LAST_REPLICATION_ACTION)).thenReturn(true);
		when(valueMap.get(BEIOConstants.LAST_REPLICATION_ACTION)).thenReturn("Activate");
		when(valueMap.get(BEIOConstants.CQ_LAST_REPLICATED,String.class)).thenReturn("2018-11-26T20:41:40.428+05:30");
		when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);
		when(resource.adaptTo(ModifiableValueMap.class)).thenReturn(map);
		when(resource.getResourceResolver()).thenReturn(resolver);
		classObj.execute(workItem, workflowSession, metaDataMap);
	}
	
	@Test
	public void testCatchBlock() throws IllegalArgumentException, LoginException, WorkflowException
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		classObj.execute(workItem, workflowSession, metaDataMap);
	}

}
