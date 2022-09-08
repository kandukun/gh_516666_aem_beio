package com.bms.beio.slingmodels;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.sightly.WCMUsePojo;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BaseModel.class,WCMUsePojo.class})
public class GenericListingComponentModelTest {
	
	@InjectMocks
	GenericListingComponentModel genericListingComponentModel;
	List<String> role=new ArrayList<>();
	List<String> therapeutic=new ArrayList<>();
	List<String> indication=new ArrayList<>();
	List<String> products=new ArrayList<>();
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResourceResolver;
	
	@Mock
	ResourceResolver resourceResolver;
	
	@Mock
	Resource root;
	
	@Mock
	ValueMap valueMap;
	
	@Mock
	Session session;
	
	@Mock
	Workspace workspace;
	
	@Mock
	TagManager tagManager;
	
	@Mock
	Tag tag;
	
	@Mock
	QueryManager queryManager;
	
	@Mock
	Query sqlQuery;
	
	@Mock
	QueryResult result;
	
	@Mock
	NodeIterator iterator;
	
	@Mock
	Resource resource;
	
	@Mock
	ProductPromoModel contentFragment;
	
	@Mock
	ProductNonPromoModel nonPromoFragment;
	
	@Mock
	BEIOPromoModel beioPromoFragment; 
		
	@Mock
	BEIONonPromoModel beiononPromoFragment;
	
	@Mock 
	Node node;
	
	private final Class<ProductPromoModel> model = ProductPromoModel.class;
	
	private final Class<ProductNonPromoModel> model1 = ProductNonPromoModel.class;
	
	private final Class<BEIOPromoModel> model2 = BEIOPromoModel.class;
	
	private final Class<BEIONonPromoModel> model3 = BEIONonPromoModel.class;
	
	private final String query = "SELECT child.* FROM [nt:unstructured] AS parent INNER JOIN [nt:unstructured] as child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent,[/content/dam/beio/ar/es_ar/promo]) AND (NAME(parent)='data' AND parent.[cq:model] = '/conf/beio-mobile-app/settings/dam/cfm/models/products')AND (NAME(child)='master') AND (child.[creationDate]>='June 25' OR child.[publishDate]>='June 25' )";
	
	private final String query1 = "SELECT child.* FROM [nt:unstructured] AS parent INNER JOIN [nt:unstructured] as child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent,[/content/dam/beio/non-promo]) AND (NAME(parent)='data' AND parent.[cq:model] = '/conf/beio-mobile-app/settings/dam/cfm/models/non-promo-product')AND (NAME(child)='master') AND (child.[creationDate]>='June 25' OR child.[publishDate]>='June 25' )";
	
	private final String query2 = "SELECT child.* FROM [nt:unstructured] AS parent INNER JOIN [nt:unstructured] as child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent,[/content/dam/beio/ar/es_ar/promo]) AND (NAME(parent)='data' AND parent.[cq:model] = '/conf/beio-mobile-app/settings/dam/cfm/models/beio-promo')AND (NAME(child)='master') AND (child.[creationDate]>='June 25' OR child.[publishDate]>='June 25' )";
	
	private final String query3 = "SELECT child.* FROM [nt:unstructured] AS parent INNER JOIN [nt:unstructured] as child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent,[/content/dam/beio/non-promo]) AND (NAME(parent)='data' AND parent.[cq:model] = '/conf/beio-mobile-app/settings/dam/cfm/models/beio-non-promo')AND (NAME(child)='master') AND (child.[creationDate]>='June 25' OR child.[publishDate]>='June 25' )";
	
	@Before
	public void setUp() throws IllegalArgumentException, LoginException, RepositoryException
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResourceResolver);
		when(autoClosingResourceResolver.getResurceResolver()).thenReturn(resourceResolver);
		when(resourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);
		when(tagManager.resolve("role 1")).thenReturn(tag);
		when(tagManager.resolve("indication 1")).thenReturn(tag);
		when(tagManager.resolve("therapeutic 1")).thenReturn(tag);
		when(tagManager.resolve("product 1")).thenReturn(tag);
		when(tag.getName()).thenReturn("tag1");
		when(resourceResolver.resolve("/content/beio/jcr:content")).thenReturn(root);
		when(root.getValueMap()).thenReturn(valueMap);
		when(valueMap.containsKey("fullSync")).thenReturn(true);
		when(valueMap.get(BEIOConstants.SYNC_DATE, String.class)).thenReturn("");
		when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
		when(session.getWorkspace()).thenReturn(workspace);
		when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);
		genericListingComponentModel.setFormtype("products");
		genericListingComponentModel.setPromosourcepath("/content/dam/beio/ar/es_ar/promo");
		genericListingComponentModel.setNonpromosourcepath("/content/dam/beio/non-promo");
		genericListingComponentModel.setTherapeutic(therapeutic);
		genericListingComponentModel.setIndication(indication);
		genericListingComponentModel.setProducts(products);
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.setSyncDate("");
	}
	
	@Test
	public void testHtlProductPromo() throws InvalidQueryException, RepositoryException
	{
		when(queryManager.createQuery(query, "JCR-SQL2")).thenReturn(sqlQuery);
		when(sqlQuery.execute()).thenReturn(result);
		when(result.getNodes()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.nextNode()).thenReturn(node);
		when(iterator.nextNode().getPath()).thenReturn("node/path/test");
		when(resourceResolver.resolve(iterator.nextNode().getPath())).thenReturn(resource);
		when(resource.adaptTo(model)).thenReturn(contentFragment);
	}
	
	@Test
	public void testHtlProductNonPromo() throws InvalidQueryException, RepositoryException
	{
		when(queryManager.createQuery(query1, "JCR-SQL2")).thenReturn(sqlQuery);
		when(sqlQuery.execute()).thenReturn(result);
		when(result.getNodes()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.nextNode()).thenReturn(node);
		when(iterator.nextNode().getPath()).thenReturn("node/path/test");
		when(resourceResolver.resolve(iterator.nextNode().getPath())).thenReturn(resource);
		when(resource.adaptTo(model1)).thenReturn(nonPromoFragment);
	}
	
	@Test
	public void testHtlBeioProductPromo() throws InvalidQueryException, RepositoryException
	{
		when(queryManager.createQuery(query2, "JCR-SQL2")).thenReturn(sqlQuery);
		when(sqlQuery.execute()).thenReturn(result);
		when(result.getNodes()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.nextNode()).thenReturn(node);
		when(iterator.nextNode().getPath()).thenReturn("node/path/test");
		when(resourceResolver.resolve(iterator.nextNode().getPath())).thenReturn(resource);
		when(resource.adaptTo(model2)).thenReturn(beioPromoFragment);
	}
	
	@Test
	public void testHtlBeioProductNonPromo() throws InvalidQueryException, RepositoryException
	{
		when(queryManager.createQuery(query3, "JCR-SQL2")).thenReturn(sqlQuery);
		when(sqlQuery.execute()).thenReturn(result);
		when(result.getNodes()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true).thenReturn(false);
		when(iterator.nextNode()).thenReturn(node);
		when(iterator.nextNode().getPath()).thenReturn("node/path/test");
		when(resourceResolver.resolve(iterator.nextNode().getPath())).thenReturn(resource);
		when(resource.adaptTo(model3)).thenReturn(beiononPromoFragment);	
	}

	
	@Test
	public void test() throws Exception
	{
		String syncDate = "June 25";
		String promosourcepath="/content/dam/beio/ar/es_ar/promo";
		String nonpromosourcepath="/content/dam/beio/non-promo";
		Assert.assertEquals("products", genericListingComponentModel.getFormtype());
		Assert.assertEquals(promosourcepath, genericListingComponentModel.getPromosourcepath());
		Assert.assertEquals(nonpromosourcepath, genericListingComponentModel.getNonpromosourcepath());
		Assert.assertEquals(therapeutic, genericListingComponentModel.getTherapeutic());
		Assert.assertEquals(indication, genericListingComponentModel.getIndication());
		Assert.assertEquals(products, genericListingComponentModel.getProducts());
		Assert.assertEquals("true", genericListingComponentModel.getDownloadable());
		Assert.assertEquals("true", genericListingComponentModel.getShareable());
		Assert.assertEquals("", genericListingComponentModel.getSyncDate());
		genericListingComponentModel.setDownloadable("");
		Assert.assertEquals("false", genericListingComponentModel.getDownloadable());
		genericListingComponentModel.setShareable("");
		Assert.assertEquals("false", genericListingComponentModel.getShareable());
		genericListingComponentModel.setSyncDate(syncDate);
		Assert.assertEquals(syncDate, genericListingComponentModel.getSyncDate());
		
		testHtlProductPromo();
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.setShareable("false");
		genericListingComponentModel.getHtlProductPromoList();
		
		testHtlProductNonPromo();
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHtlProductNonPromoList();
		
		testHtlBeioProductPromo();
		genericListingComponentModel.getHTLBEIOPromoList();		
		testHtlBeioProductPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHTLBEIOPromoList();
		testHtlBeioProductPromo();
		genericListingComponentModel.setShareable("false");
		genericListingComponentModel.getHTLBEIOPromoList();		
		testHtlBeioProductPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHTLBEIOPromoList();
		
		
		testHtlBeioProductNonPromo();	
		genericListingComponentModel.getHTLBEIONonPromoList();		
		testHtlBeioProductNonPromo();	
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHTLBEIONonPromoList();
		testHtlBeioProductNonPromo();	
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHTLBEIONonPromoList();
		testHtlBeioProductNonPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHTLBEIONonPromoList();
		
		
		
		genericListingComponentModel.getProductPromoList();
		genericListingComponentModel.getProductNonPromoList();
		genericListingComponentModel.getBEIOPromoList();
		genericListingComponentModel.getBEIONonPromoList();
		
		
		List<String> list=new LinkedList<>();
		list.add("attribute 1");
		Whitebox.getField(genericListingComponentModel.getClass(), "filterAttributes").set(genericListingComponentModel, list);
		testHtlBeioProductPromo();
		genericListingComponentModel.getHTLBEIOPromoList();
		testHtlBeioProductPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHTLBEIOPromoList();
		testHtlBeioProductPromo();
		genericListingComponentModel.setShareable("false");
		genericListingComponentModel.getHTLBEIOPromoList();		
		testHtlBeioProductPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHTLBEIOPromoList();
		testHtlBeioProductNonPromo();
		genericListingComponentModel.getHTLBEIONonPromoList();		
		testHtlBeioProductNonPromo();	
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHTLBEIONonPromoList();
		testHtlBeioProductNonPromo();	
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHTLBEIONonPromoList();
		testHtlBeioProductNonPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHTLBEIONonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setShareable("false");
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductNonPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHtlProductNonPromoList();
		testHtlProductPromo();
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setDownloadable("false");
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setShareable("true");
		genericListingComponentModel.getHtlProductPromoList();
		testHtlProductPromo();
		genericListingComponentModel.setDownloadable("true");
		genericListingComponentModel.getHtlProductPromoList();
		
		
		genericListingComponentModel.setPromosourcepath("");
		Assert.assertEquals("", genericListingComponentModel.getPromosourcepath());
		genericListingComponentModel.getProductPromoList();
		genericListingComponentModel.getBEIOPromoList();
		
		
		genericListingComponentModel.setNonpromosourcepath("");
		Assert.assertEquals("", genericListingComponentModel.getNonpromosourcepath());
		genericListingComponentModel.getProductNonPromoList();
		genericListingComponentModel.getBEIONonPromoList();
	}
	
	@Test
	public void afterInitializationTest() throws Exception {
		role.add("role 1");
		genericListingComponentModel.setRole(role);
		indication.add("indication 1");
		genericListingComponentModel.setIndication(indication);
		therapeutic.add("therapeutic 1");
		genericListingComponentModel.setTherapeutic(therapeutic);
		products.add("product 1");
		genericListingComponentModel.setProducts(products);
		Whitebox.invokeMethod(genericListingComponentModel, "afterInitialization");
	}
	
	@Test
	public void afterInitializationTestCatchBlock() throws Exception
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		Whitebox.invokeMethod(genericListingComponentModel, "afterInitialization");
	}
}
