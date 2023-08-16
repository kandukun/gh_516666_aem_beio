package com.bms.beio.utils;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;

/**
 * @author marri.shashanka
 *
 * @param <T>
 * 
 * This is a generic class used to construct a list of content fragments.
 */
public class ContentFragmentHelper<T>{

	Logger LOG = LoggerFactory.getLogger(ContentFragmentHelper.class);
	
	/**
	 * @param resourceResolver
	 * @param query
	 * @param model
	 * @return List of Content Fragments of Mentioned Type.
	 * 
	 * This method executes a query and returns a list of either Communications or Documents or Links 
	 * as mentioned in the Type Parameter. 
	 */
	public List<T> getFragmentList(BEIOAutoClosingResourceResolverFactory autoClosingResourceResolver, String query, Class<T> model,String serviceUser) {
		LOG.debug("::::::: Entered getFragmentList method of ContentFragmentHelper :::::::");
		LinkedList<T> contentList =  new LinkedList<>();
		
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolver.getResourceResolver(serviceUser)){
			ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
			if (null != resourceResolver) {
				Session session = resourceResolver.adaptTo(Session.class);
				if (null != session) {
					try {
						if (null != session.getWorkspace()) {
							QueryManager queryManager = session.getWorkspace().getQueryManager();
							if (null != queryManager) {
								Query sqlQuery = queryManager.createQuery(query, "JCR-SQL2");
								if (null != sqlQuery) {
									QueryResult result = sqlQuery.execute();
									if (null != result) {
										NodeIterator iterator = result.getNodes();
										if (null != iterator) {
											while (iterator.hasNext()) {
												Resource resource = resourceResolver.resolve(iterator.nextNode().getPath());
												LOG.info("resource path: "+ resource);
												T contentFragment = resource.adaptTo(model);
												if (null != contentFragment) {
													contentList.add(contentFragment);
												}
											}
										}
									}
								}
							}
						}
					} catch (RepositoryException e) {
						LOG.error(" ::::::: Exception in executing query in getFragmentList Method of ContentFragmentHelper class :::::::" , e); 
					}
				}else {
					LOG.debug("::::::: Session is NULL in getFragmentList method of ContentFragmentHelper :::::::");
				}
			}
			
		} catch (IllegalArgumentException | LoginException e) {
			LOG.error(" ::::::: Exception in getFragmentList Method of ContentFragmentHelper class ::::::: " , e);
		} catch(Exception ex) {
			LOG.error(" ::::::: Broader Exception in getFragmentList Method of ContentFragmentHelper class ::::::: " , ex);
		}
		LOG.debug("::::::: Exit from getFragmentList method of ContentFragmentHelper :::::::");
		LOG.info("Content list: "+contentList);
		return contentList;
	}
}