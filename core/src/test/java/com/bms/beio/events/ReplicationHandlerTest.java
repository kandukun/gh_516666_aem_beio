package com.bms.beio.events;
import javax.jcr.Session;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;

import com.bms.beio.config.BmsBeioConfig;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.utils.CommonUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReplicationAction.class, CommonUtils.class })
public class ReplicationHandlerTest {
	
	@InjectMocks
	ReplicationHandler replicationHandler;
	
	@Mock
	ResourceResolverFactory resourceResolverFactory;	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResolver;
	@Mock
	private ComponentContext context;
	@Mock
	BmsBeioConfig bmsConfig;
	@Mock
	Event event;
	@Mock
	SlingSettingsService slingSettingService;
	@Mock
	ReplicationAction action;
	@Mock
	Resource replicatedResource;
	@Mock
	Resource jcrContent;
	@Mock
	ValueMap jcrValueMap;
	@Mock
	Resource data;
	@Mock
	ValueMap dataValueMap;
	@Mock
	Resource master;
	@Mock
	ModifiableValueMap modifiableValueMap;
	@Mock
	Session currentSession;
	@Mock
	ResourceResolver resourceResolver;
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(ReplicationAction.class);
		PowerMockito.mockStatic(CommonUtils.class);	
		Set<String> runModes = Mockito.mock(HashSet.class);
		when(slingSettingService.getRunModes()).thenReturn(runModes);
		when(runModes.contains("author")).thenReturn(true);
	}
	
	@Test
	public void testActivate() throws Exception{
		
		/*autoClosingResourceResolverFactory = mock(AutoClosingResourceResolverFactory.class);
		resourceResolverFactory = mock(ResourceResolverFactory.class);
		autoClosingResolver = mock(AutoClosingResourceResolver.class);
		currentSession = mock(Session.class);
		 context=mock(ComponentContext.class);
		 bmsConfig=mock(BmsCfmConfig.class);
		 event=mock(Event.class);
		 slingSettingService=mock(SlingSettingsService.class);
		 action=mock(ReplicationAction.class);
		 replicatedResource=mock(Resource.class);
		 jcrContent=mock(Resource.class);
		 jcrValueMap=mock(ValueMap.class);
		 data=mock(Resource.class);
		 dataValueMap=mock(ValueMap.class);
		 master=mock(Resource.class);
		 modifiableValueMap=mock(ModifiableValueMap.class);
		 resourceResolver = mock(ResourceResolver.class);
					
		*/
		when(resourceResolverFactory.getServiceResourceResolver(Mockito.anyMap())).thenReturn(resourceResolver);
		when(bmsConfig.getDamRoot()).thenReturn("/content/beio/image.jpeg");
		replicationHandler.activate(context);
		
		
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResolver);
		when(autoClosingResolver.getResurceResolver()).thenReturn(resourceResolver);
		
			
		PowerMockito.when(ReplicationAction.fromEvent(event)).thenReturn(action);
		when(action.getType()).thenReturn(ReplicationActionType.ACTIVATE);
		when(action.getPath()).thenReturn("/content/beio/image.jpeg");
		when(resourceResolver.resolve("/content/beio/image.jpeg")).thenReturn(replicatedResource);
		PowerMockito.when(CommonUtils.getProperty(replicatedResource, JcrConstants.JCR_PRIMARYTYPE)).thenReturn("dam:Asset");
		when(replicatedResource.getChild(JcrConstants.JCR_CONTENT)).thenReturn(jcrContent);
		PowerMockito.when(CommonUtils.checkResource(jcrContent)).thenReturn(true);
		when(jcrContent.getValueMap()).thenReturn(jcrValueMap);
		when(jcrValueMap.containsKey(BEIOConstants.CONTENT_FRAGMENT)).thenReturn(true).thenReturn(false);
		when(jcrValueMap.get(BEIOConstants.CONTENT_FRAGMENT,Boolean.class)).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
		when(jcrContent.getChild(BEIOConstants.DATA)).thenReturn(data);
		PowerMockito.when(CommonUtils.checkResource(data)).thenReturn(true);
		when(data.getValueMap()).thenReturn(dataValueMap);
		when(dataValueMap.containsKey("cq:model")).thenReturn(true).thenReturn(false);
		when(dataValueMap.get("cq:model",String.class)).thenReturn("/conf/beio-mobile-app/settings/dam/cfm/models/news");
		when(data.getChild(BEIOConstants.MASTER)).thenReturn(master);
		PowerMockito.when(CommonUtils.checkResource(master)).thenReturn(true);
		when(master.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);
		when(resourceResolver.adaptTo(Session.class)).thenReturn(currentSession);		
		replicationHandler.handleEvent(event);
		
		when(dataValueMap.get("cq:model",String.class)).thenReturn("/conf/beio-mobile-app/settings/dam/cfm/models/products");
		replicationHandler.handleEvent(event);
		
		when(dataValueMap.get("cq:model",String.class)).thenReturn("/conf/beio-mobile-app/settings/dam/cfm/models/non-promo-product");
		replicationHandler.handleEvent(event);
		
		when(dataValueMap.get("cq:model",String.class)).thenReturn("/conf/beio-mobile-app/settings/dam/cfm/models/beio-promo");
		replicationHandler.handleEvent(event);
		
		when(dataValueMap.get("cq:model",String.class)).thenReturn("/conf/beio-mobile-app/settings/dam/cfm/models/beio-non-promo");
		when(action.getType()).thenReturn(ReplicationActionType.DEACTIVATE);
		when(modifiableValueMap.containsKey(BEIOConstants.PUBLISH_DATE)).thenReturn(true);
		replicationHandler.handleEvent(event);
	}
	
	@Test
	public void testCatchBlock() throws Exception
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		replicationHandler.handleEvent(event);
	}
	
	@Test
	public void testActivateEmpty() {
		when(bmsConfig.getDamRoot()).thenReturn("");
		replicationHandler.activate(context);
	}
}
