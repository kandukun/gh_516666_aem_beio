package com.bms.beio.resource;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author marri.shashanka
 *
 *This is a wrapper class, wrapping a ResourceReource Resolver
 *which will auto close after finishing its execution scope.
 *
 */
public interface BEIOAutoClosingResourceResolver extends AutoCloseable {

	public ResourceResolver getResurceResolver();

	public Session getSession();

}
