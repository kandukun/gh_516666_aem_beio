package com.bms.beio.events;

import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.ObservationManager;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.service.component.ComponentContext;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bms.beio.config.BmsBeioConfig;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.day.cq.commons.jcr.JcrConstants;
@RunWith(PowerMockRunner.class)
public class BEIOAssetHandlerTest { 
    
	@InjectMocks
	BEIOAssetHandler assetHandler;
	@Mock
	ResourceResolverFactory resoureResolverFactory;
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResolver;	
	@Mock
	private ResourceResolver resourceResolver;
	@Mock
	private Session session;
	@Mock
	private ObservationManager observationManager;
	@Mock
	private ComponentContext context;
	@Mock
	private Workspace workspace;
	@Mock
	private EventIterator events;
	@Mock
	Event event;
	@Mock
	Resource jcr_content;
	@Mock
	Resource parent;
	@Mock
	ValueMap parentValueMap;
	@Mock
	Resource data;
	@Mock
	Resource master;
	@Mock
	ModifiableValueMap valueMap;
	@Mock
	SlingSettingsService slingSettingService;
	@Mock
	BmsBeioConfig bmsConfig;
	
	@Before
	public void setUp() throws RepositoryException
	{
		Set<String> runModes = Mockito.mock(HashSet.class);
		when(slingSettingService.getRunModes()).thenReturn(runModes);
		when(runModes.contains("author")).thenReturn(true);
		when(events.hasNext()).thenReturn(true).thenReturn(false);
		when(events.nextEvent()).thenReturn(event);
		when(event.getPath()).thenReturn("/url/path :jcr:content");
		when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
	}
	
	@Test
	public void testActivate() throws Exception{
		Set<String> runModes = Mockito.mock(HashSet.class);
		when(slingSettingService.getRunModes()).thenReturn(runModes);
		when(runModes.contains("author")).thenReturn(true);
		when(bmsConfig.getDamRoot()).thenReturn("");
		when(resoureResolverFactory.getServiceResourceResolver(Mockito.anyMap())).thenReturn(resourceResolver);
		when(session.getWorkspace()).thenReturn(workspace);
		when(workspace.getObservationManager()).thenReturn(observationManager);
		assetHandler.activate(context);
		when(resourceResolver.adaptTo(Session.class)).thenReturn(null);
		assetHandler.activate(context);
		assetHandler.activate(context);		
	}
	
	@Test
	public void testOnEvent() throws RepositoryException, IllegalArgumentException, LoginException{
		String path = "/url/path :jcr:content";
		
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResolver);
		when(autoClosingResolver.getResurceResolver()).thenReturn(resourceResolver);
		
		when(resourceResolver.resolve(path)).thenReturn(jcr_content);
		when(jcr_content.getValueMap()).thenReturn(valueMap);
		when(jcr_content.getValueMap().containsKey(BEIOConstants.CONTENT_FRAGMENT)).thenReturn(true).thenReturn(false);
		when(jcr_content.getValueMap().get(BEIOConstants.CONTENT_FRAGMENT, Boolean.class)).thenReturn(true).thenReturn(false);
		when(jcr_content.getParent()).thenReturn(parent);
		when(parent.getValueMap()).thenReturn(parentValueMap);
		when(parentValueMap.containsKey(JcrConstants.JCR_CREATED)).thenReturn(true).thenReturn(false);
		when(parentValueMap.get(JcrConstants.JCR_CREATED, String.class)).thenReturn("2018-07-10T15:42:22.152+05:30");
		when(jcr_content.getChild(BEIOConstants.DATA)).thenReturn(data);
		when(data.getChild(BEIOConstants.MASTER)).thenReturn(master);
		when(master.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
		assetHandler.onEvent(events);
	}
	
	@Test
	public void testCatchBlock() throws Exception
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		assetHandler.onEvent(events);
	}
} 