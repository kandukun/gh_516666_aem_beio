package com.bms.beio.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.constants.BEIOConstants;

public class QueryBuilder {
	
	static Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);
	
	public static String buildServletQuery(String path,String fragmentModel,String publishDate, boolean isPublish) {
		LOG.debug(" :::::::: Entered buildServletQuery method of QueryBuilder class ::::::: ");
		String servlet_query_string = "SELECT child.* FROM [" + JcrConstants.NT_UNSTRUCTURED
				+ "] AS parent INNER JOIN [" + JcrConstants.NT_UNSTRUCTURED
				+ "] as child ON ISCHILDNODE(child,parent) " + "WHERE ISDESCENDANTNODE(parent,["
				+ path + "]) AND (NAME(parent)='" + BEIOConstants.DATA + "' "
				+ "AND parent.[cq:model] = '" + fragmentModel + "')"
				+ "AND (NAME(child)='" + BEIOConstants.MASTER + "')";
		
		if(StringUtils.isNotBlank(publishDate)) {
			if(isPublish) {
				servlet_query_string = servlet_query_string + " AND (child.[publishDate]>='" + publishDate + "')";
			}else {
				servlet_query_string = servlet_query_string + " AND (child.[creationDate]>='" + publishDate + "' OR child.[publishDate]>='" + publishDate + "' )";
			}
		}
		LOG.debug(" :::::::: Exit from buildServletQuery method of QueryBuilder class ::::::: ");
		LOG.debug("Query is ::::::: " + servlet_query_string);
		return servlet_query_string;
	}
	
	/**
	 * @param path
	 * @param fragmentModel
	 * @param lastReplicatedDate
	 * @return query string
	 * 
	 * Constructs a query to fetch the Content fragments whose last published date is greater 
	 * than the preview page on which workflow has been started.. 
	 */
	public static String buildQueryToFetchFragments(String path, String fragmentModel, String lastReplicatedDate) {
		LOG.debug("::::::: Entered buildFetchFragmentsQuery Method of QueryBuilder class :::::::");
		String selector_query_string = "SELECT child.* FROM [" + JcrConstants.NT_UNSTRUCTURED
				+ "] AS parent INNER JOIN [" + JcrConstants.NT_UNSTRUCTURED
				+ "] as child ON ISCHILDNODE(child,parent) " + "WHERE ISDESCENDANTNODE(parent,["
				+ path + "]) AND (NAME(parent)='" + BEIOConstants.DATA + "' "
				+ "AND parent.[cq:model] = '" + fragmentModel + "') "
				+ "AND (NAME(child)='" + BEIOConstants.MASTER + "') ";
		
		String syncCriteria = StringUtils.EMPTY;
		if(StringUtils.isNotBlank(lastReplicatedDate)) {
			syncCriteria = syncCriteria.concat(BEIOConstants.AND).concat("( child.[publishDate]>='").concat(lastReplicatedDate).concat("')");	
		}
		selector_query_string = selector_query_string.concat(syncCriteria);
		LOG.debug("::::::: Exit from buildFetchFragmentsQuery Method of QueryBuilder class :::::::"+selector_query_string);
		return selector_query_string;
	}

}
