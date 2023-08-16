package com.bms.beio.resource;

import java.util.Map;

import org.apache.sling.api.resource.LoginException;

public interface BEIOAutoClosingResourceResolverFactory {
	
	public BEIOAutoClosingResourceResolver getResourceResolver(String serviceUser) throws IllegalArgumentException, LoginException;
	
	public BEIOAutoClosingResourceResolver getResourceResolver(Map<String, Object>authMap)throws IllegalArgumentException, LoginException;
	
}
