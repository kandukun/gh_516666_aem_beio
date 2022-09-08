package com.bms.beio.utils;

import static org.mockito.Mockito.mock;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.ProductPromoModel;

import static org.mockito.Mockito.when;

public class ContentFragmentHelperTest {
	
	@Mock
	ResourceResolver resourceResolver;
	
	@Mock
	Session session;
	
	@Mock
	Workspace workspace;
	
	@Mock
	QueryManager queryManager;
	
	@Mock
	Query sqlQuery;
	
	@Mock
	QueryResult result;
	
	@Mock
	NodeIterator iterator;
	
	@Mock 
	Node node;
	
	@Mock
	Resource resource;
	
	@Mock
	ProductPromoModel contentFragment;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResolver;
	
	private final String query = "Testquery";
	
	private final Class<ProductPromoModel> model = ProductPromoModel.class;
	
	ContentFragmentHelper<ProductPromoModel> contentFragmentHelper = new ContentFragmentHelper<ProductPromoModel>();
	
	@Before
	public void setUp() throws RepositoryException, IllegalArgumentException, LoginException{
		resourceResolver = mock(ResourceResolver.class);
		autoClosingResourceResolverFactory = mock(BEIOAutoClosingResourceResolverFactory.class);
		session = mock(Session.class);
		workspace = mock(Workspace.class);
		queryManager = mock(QueryManager.class);
		sqlQuery = mock(Query.class);
		result = mock(QueryResult.class);
		iterator = mock(NodeIterator.class);
		resource = mock(Resource.class);
		node = mock(Node.class);
		autoClosingResolver = mock(BEIOAutoClosingResourceResolver.class);
		contentFragment = mock(ProductPromoModel.class);
		
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoClosingResolver);
		when(autoClosingResolver.getResurceResolver()).thenReturn(resourceResolver);
		when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
		when(session.getWorkspace()).thenReturn(workspace);
		when(session.getWorkspace().getQueryManager()).thenReturn(queryManager);
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
	public void test() {
		contentFragmentHelper.getFragmentList(autoClosingResourceResolverFactory, query, model,BEIOConstants.BEIO_ADMIN);
	}
	
	@Test
	public void testElseBlock() {
		when(resourceResolver.adaptTo(Session.class)).thenReturn(null);
		contentFragmentHelper.getFragmentList(autoClosingResourceResolverFactory, query, model,BEIOConstants.BEIO_ADMIN);
	}
	
	@Test
	public void testCatchBlock() throws IllegalArgumentException, LoginException
	{
		when(autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(null);
		contentFragmentHelper.getFragmentList(autoClosingResourceResolverFactory, query, model,BEIOConstants.BEIO_ADMIN);
	}

}