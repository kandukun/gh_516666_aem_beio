package com.bms.beio.resource;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

public final class AutoClosingResourceResolverUtil {

	public static BEIOAutoClosingResourceResolver getResourceResolver(ResourceResolverFactory resourceResolverFactory,
			String serviceUser) throws LoginException, IllegalArgumentException {
		if (StringUtils.isEmpty(serviceUser)) {
			throw new IllegalArgumentException("ServiceUser Cannot be null ");
		}
		Map<String, Object> authMap = new HashMap<>();
		authMap.put(ResourceResolverFactory.SUBSERVICE, serviceUser);
		return getResourceResolver(resourceResolverFactory, authMap);
	}

	public static BEIOAutoClosingResourceResolver getResourceResolver(ResourceResolverFactory resourceResolverFactory,
			Map<String, Object> authMap) throws LoginException, IllegalArgumentException {

		if (null == authMap) {
			throw new IllegalArgumentException("AuthMap Cannot be null");
		}

		return new AutoClosingResourceResolverImpl(resourceResolverFactory.getServiceResourceResolver(authMap));
	}

	private static class AutoClosingResourceResolverImpl implements BEIOAutoClosingResourceResolver {

		private ResourceResolver resourceResolver;
		private Session session;

		AutoClosingResourceResolverImpl(ResourceResolver resourceResolver) {
			this.resourceResolver = resourceResolver;
			this.session = resourceResolver.adaptTo(Session.class);
		}

		@Override
		public void close() throws Exception {
			if (null != resourceResolver && resourceResolver.isLive()) {
				resourceResolver.close();
				resourceResolver = null;
			}
		}

		@Override
		public ResourceResolver getResurceResolver() {
			return resourceResolver;
		}

		@Override
		public Session getSession() {
			return session;
		}

	}

}
