package com.bms.beio.resource.impl;

import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.resource.AutoClosingResourceResolverUtil;


@Component(service=BEIOAutoClosingResourceResolverFactory.class,immediate=true)
public class AutoClosingResourceResolverFactoryImpl implements BEIOAutoClosingResourceResolverFactory{

	@Reference
	ResourceResolverFactory resourceResolverFactory;
	
	@Override
	public BEIOAutoClosingResourceResolver getResourceResolver(String serviceUser) throws IllegalArgumentException, LoginException {
		return AutoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory,serviceUser );
	}

	@Override
	public BEIOAutoClosingResourceResolver getResourceResolver(Map<String, Object> authMap) throws IllegalArgumentException, LoginException {
		return AutoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory,authMap);
	}

}
