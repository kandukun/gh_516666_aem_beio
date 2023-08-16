package com.bms.beio.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BaseModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;


@RunWith(PowerMockRunner.class)
public class BEIOUtilsTest {

	BEIOUtils beioUtils=new BEIOUtils();
	
	@Mock
	ProductPromoModel product;
	
	@Mock
	ProductNonPromoModel nonpromoproduct;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResourceResolver;
	
	@Mock
	ResourceResolver resourceResolver;
	
	@Mock
	TagManager tagManager;
	
	@Mock
	Tag tag;
	
	@Before
	public void setUp() throws IllegalArgumentException, LoginException
	{
		product=mock(ProductPromoModel.class);
		nonpromoproduct=mock(ProductNonPromoModel.class);
		autoClosingResourceResolverFactory = mock(BEIOAutoClosingResourceResolverFactory.class);
		autoClosingResourceResolver = mock(BEIOAutoClosingResourceResolver.class);
		resourceResolver= mock(ResourceResolver.class);
		tagManager = mock(TagManager.class);
		tag = mock(Tag.class);
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResourceResolver);
		when(autoClosingResourceResolver.getResurceResolver()).thenReturn(resourceResolver);
		when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
		when(tagManager.resolve(Mockito.anyString())).thenReturn(tag);
		when(tag.getName()).thenReturn("one");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void test() throws IllegalArgumentException, LoginException
	{
		List<String> alignmentData = new ArrayList<>();
		
		beioUtils.getBEIONonPromoGenericPredicate(alignmentData, "role");
		beioUtils.getBEIONonPromoGenericPredicate(alignmentData, "therapeuticarea");
		beioUtils.getBEIONonPromoGenericPredicate(alignmentData, "indication");
		beioUtils.getBEIONonPromoGenericPredicate(alignmentData, "products");
		beioUtils.getBEIONonPromoGenericPredicate(alignmentData, null);
		
		beioUtils.getBEIOPromoGenericPredicate(alignmentData, "role");
		beioUtils.getBEIOPromoGenericPredicate(alignmentData, "therapeuticarea");
		beioUtils.getBEIOPromoGenericPredicate(alignmentData, "indication");
		beioUtils.getBEIOPromoGenericPredicate(alignmentData, "products");
		beioUtils.getBEIOPromoGenericPredicate(alignmentData, null);
		
		
		beioUtils.getProductsPromoGenericPredicate(alignmentData, "therapeuticarea");
		beioUtils.getProductsPromoGenericPredicate(alignmentData, "indication");	
		beioUtils.getProductsPromoGenericPredicate(alignmentData, "products");
		beioUtils.getProductsPromoGenericPredicate(alignmentData, "role");
		beioUtils.getProductsPromoGenericPredicate(alignmentData, null);
		
		beioUtils.getProductsNonPromoGenericPredicate(alignmentData, "therapeuticarea");
		beioUtils.getProductsNonPromoGenericPredicate(alignmentData, "indication");
		beioUtils.getProductsNonPromoGenericPredicate(alignmentData, "products");
		beioUtils.getProductsNonPromoGenericPredicate(alignmentData, "role");
		beioUtils.getProductsNonPromoGenericPredicate(alignmentData, null);	
		
		beioUtils.getNewsPredicate(alignmentData, autoClosingResourceResolverFactory);
	}
	@Test
	public void listCheckTest() throws Exception {
		BEIOUtils beioUtils = new BEIOUtils();
		List<String> nullValue = null;
		Whitebox.invokeMethod(beioUtils, "listCheck", nullValue, nullValue);
		List<String> fragmentTags = new ArrayList<String>();
		fragmentTags.add("one");
		Whitebox.invokeMethod(beioUtils, "listCheck", nullValue, fragmentTags);
		List<String> alignmentData = new ArrayList<String>();
		alignmentData.add("one");
		Whitebox.invokeMethod(beioUtils, "listCheck", alignmentData, nullValue);
		Whitebox.invokeMethod(beioUtils, "listCheck", alignmentData, fragmentTags);
		List<String> alignmentData1 = new ArrayList<String>();
		List<String> fragmentTags1 = new ArrayList<String>();
		Whitebox.invokeMethod(beioUtils, "listCheck", alignmentData1, fragmentTags1);
	}
	@Test
	public void filterModelsTest() throws Exception {
		BEIOUtils beioUtils = new BEIOUtils();
		List<String> alignmentData = new ArrayList<String>();
		alignmentData.add("one");
		BaseModel baseModel = new BaseModel();
		baseModel.setCfUniqueID("cfUniqueID");
		baseModel.setCreationDate("creationDate");
		baseModel.setPublishDate("publishDate");
		baseModel.setRole(alignmentData);
		Whitebox.invokeMethod(beioUtils, "filterModels", autoClosingResourceResolverFactory, alignmentData,baseModel);
	}
	
	@Test
	public void filterModelsTestCatchBlock() throws Exception {
		BEIOUtils beioUtils = new BEIOUtils();
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		Whitebox.invokeMethod(beioUtils, "filterModels", autoClosingResourceResolverFactory, null,null);
	}
}
