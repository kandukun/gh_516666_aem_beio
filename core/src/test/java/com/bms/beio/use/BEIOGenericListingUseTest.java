package com.bms.beio.use;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.adobe.cq.sightly.WCMUsePojo;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.utils.CommonUtils;
import com.bms.beio.utils.QueryBuilder;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.bms.bmscorp.core.utils.BMSResourceResolverUtil;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Collectors.class, WCMUsePojo.class, QueryBuilder.class, BMSResourceResolverUtil.class, CommonUtils.class})
public class BEIOGenericListingUseTest {

	@Mock
	SlingScriptHelper slingScriptHelper;
	@Mock
	BEIOAutoClosingResourceResolverFactory autoResolverFactory;
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResourceResolver;
	@Mock
	ResourceResolver resourceResolver;
	@Mock
	Page page;
	@Mock
	Resource root;
	@Mock
	Resource resource;
	@Mock
	GenericListingComponentModel genericListingComponentModel;
	@Mock
	Iterable<Resource> children;
	@Mock
	Iterator<Resource> iterator;
	@Mock
	Resource testResource;
	
	BEIOGenericListingUse beiogenericListingUse = new BEIOGenericListingUse();
	
	private static BEIOGenericListingUse underTest;
	
	@Before
	public void setUp() throws Exception {
		slingScriptHelper = mock(SlingScriptHelper.class);
		page=mock(Page.class);
		autoResolverFactory = mock(BEIOAutoClosingResourceResolverFactory.class);
		autoClosingResourceResolver = mock(BEIOAutoClosingResourceResolver.class);
		resourceResolver=mock(ResourceResolver.class);
		root=mock(Resource.class);
		children=mock(Iterable.class);
		iterator=mock(Iterator.class);
		resource=mock(Resource.class);
		underTest = PowerMockito.spy(new BEIOGenericListingUse());
		
		PowerMockito.doReturn(slingScriptHelper).when((WCMUsePojo) underTest).getSlingScriptHelper();
		PowerMockito.doReturn(page).when((WCMUsePojo) underTest).getCurrentPage();
		
		when(slingScriptHelper.getService(BEIOAutoClosingResourceResolverFactory.class)).thenReturn(autoResolverFactory);
		String path="/content/beio";
		when(page.getPath()).thenReturn(path);
		when(autoResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResourceResolver);
		when(autoClosingResourceResolver.getResurceResolver()).thenReturn(resourceResolver);
		when(resourceResolver.resolve("/content/beio/jcr:content/root")).thenReturn(root);
		when(root.getChildren()).thenReturn(children);
		when(children.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.next()).thenReturn(resource);
		when(resource.getResourceType()).thenReturn("beio-mobile-app/components/content/beio-generic-listing");
		when(resource.adaptTo(GenericListingComponentModel.class)).thenReturn(genericListingComponentModel);
		beiogenericListingUse.setGenericListingComponentModel(genericListingComponentModel);		
	}
	
	@Test
	public void test() throws Exception
	{
		Assert.assertEquals(genericListingComponentModel, beiogenericListingUse.getGenericListingComponentModel());
		underTest.activate();		
	}
	
	@Test
	public void testCatchBlock() throws Exception
	{
		when(slingScriptHelper.getService(BEIOAutoClosingResourceResolverFactory.class)).thenReturn(null);
		underTest.activate();
	}
}
