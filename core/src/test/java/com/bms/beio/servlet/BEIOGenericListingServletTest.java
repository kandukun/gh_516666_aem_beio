package com.bms.beio.servlet;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.bmscorp.core.service.BMSDomainFactory;

@RunWith(PowerMockRunner.class)
public class BEIOGenericListingServletTest {

	@InjectMocks
	BEIOGenericListingServlet beioGenericListingServlet;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResourceResolver;
	
	@Mock
	ResourceResolver resourceResolver;
	
	@Mock
	private SlingHttpServletRequest request;
	
	@Mock
	private SlingHttpServletResponse response;
	
	@Mock
	RequestPathInfo requestPathInfo;
	
	@Mock
	Resource root; 
	
	@Mock
	Iterable<Resource> children;
	
	@Mock
	Iterator<Resource> iterator;
	
	@Mock
	Resource resource;
	
	@Mock
	GenericListingComponentModel genericListingModel;
	
	@Mock
	PrintWriter printWriter;
	
	@Mock
	BMSDomainFactory bmsDomainFactory;
	
	@Before
	public void setUp() throws IllegalArgumentException, LoginException
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResourceResolver);
		when(autoClosingResourceResolver.getResurceResolver()).thenReturn(resourceResolver);		
	}
	
	@Test
	public void resusableCode() throws IOException
	{
		String[] selectors = {"selector1","selector2","checkdate","imm_rbd","10-018-319"};
		when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
		when(requestPathInfo.getResourcePath()).thenReturn("/content");
		when(requestPathInfo.getSelectors()).thenReturn(selectors);
		when(root.getChildren()).thenReturn(children);
		when(children.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.next()).thenReturn(resource);
		when(resourceResolver.resolve("/content/root")).thenReturn(root);
		when(resource.getResourceType()).thenReturn("beio-mobile-app/components/content/beio-generic-listing");
		when(resource.adaptTo(GenericListingComponentModel.class)).thenReturn(genericListingModel);
		when(genericListingModel.getPromosourcepath()).thenReturn("/content/promo");
		when(response.getWriter()).thenReturn(printWriter);		
	}
	
	@Test
	public void test() throws ServletException, IOException
	{	
		resusableCode();
		when(genericListingModel.getFormtype()).thenReturn("products");
		beioGenericListingServlet.doGet(request, response);
		
		resusableCode();
		when(genericListingModel.getFormtype()).thenReturn("all");
		beioGenericListingServlet.doGet(request, response);
		
		String[] selectors= {};
		List<ProductPromoModel> productsPromoList=new ArrayList<>();
		ProductPromoModel productpromomodel=new ProductPromoModel();
		productsPromoList.add(productpromomodel);
		when(genericListingModel.getProductPromoList()).thenReturn(productsPromoList);
		List<ProductNonPromoModel> productsNonPromoList=new ArrayList<>();
		ProductNonPromoModel productnonpromomodel=new ProductNonPromoModel();
		productsNonPromoList.add(productnonpromomodel);
		when(genericListingModel.getProductNonPromoList()).thenReturn(productsNonPromoList);
		when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
		when(requestPathInfo.getResourcePath()).thenReturn("/content");
		when(requestPathInfo.getSelectors()).thenReturn(selectors);
		when(root.getChildren()).thenReturn(children);
		when(children.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.next()).thenReturn(resource);
		when(resourceResolver.resolve("/content/root")).thenReturn(root);
		when(resource.getResourceType()).thenReturn("beio-mobile-app/components/content/beio-generic-listing");
		when(resource.adaptTo(GenericListingComponentModel.class)).thenReturn(genericListingModel);
		when(genericListingModel.getPromosourcepath()).thenReturn("/content/promo");
		when(response.getWriter()).thenReturn(printWriter);
		when(genericListingModel.getFormtype()).thenReturn("products");
		beioGenericListingServlet.doGet(request, response);
		
		String[] selectors1= {};
		List<BEIOPromoModel> beioPromoList=new ArrayList<>();
		BEIOPromoModel beiopromomodel=new BEIOPromoModel();
		beioPromoList.add(beiopromomodel);
		when(genericListingModel.getBEIOPromoList()).thenReturn(beioPromoList);
		List<BEIONonPromoModel> beioNonPromoList=new ArrayList<>();
		BEIONonPromoModel beiononpromomodel=new BEIONonPromoModel();
		beioNonPromoList.add(beiononpromomodel);
		when(genericListingModel.getBEIONonPromoList()).thenReturn(beioNonPromoList);
		when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
		when(requestPathInfo.getResourcePath()).thenReturn("/content");
		when(requestPathInfo.getSelectors()).thenReturn(selectors1);
		when(root.getChildren()).thenReturn(children);
		when(children.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.next()).thenReturn(resource);
		when(resourceResolver.resolve("/content/root")).thenReturn(root);
		when(resource.getResourceType()).thenReturn("beio-mobile-app/components/content/beio-generic-listing");
		when(resource.adaptTo(GenericListingComponentModel.class)).thenReturn(genericListingModel);
		when(genericListingModel.getPromosourcepath()).thenReturn("/content/promo");
		when(response.getWriter()).thenReturn(printWriter);
		when(genericListingModel.getFormtype()).thenReturn("all");
		beioGenericListingServlet.doGet(request, response);
	}
	
}
