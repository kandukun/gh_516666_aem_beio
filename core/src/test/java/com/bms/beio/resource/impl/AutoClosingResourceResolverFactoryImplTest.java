package com.bms.beio.resource.impl;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.AutoClosingResourceResolverUtil;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;

@RunWith(PowerMockRunner.class)
public class AutoClosingResourceResolverFactoryImplTest {

		@Mock
		private BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory= new AutoClosingResourceResolverFactoryImpl();
		@InjectMocks
		private AutoClosingResourceResolverFactoryImpl autoClosingResourceResolverFactoryImpl= new AutoClosingResourceResolverFactoryImpl();
		AutoClosingResourceResolverUtil autoClosingResourceResolverUtil;
		@Mock
		ResourceResolverFactory resourceResolverFactory;		
		@Mock
		BEIOAutoClosingResourceResolver autoClosingResourceResolver;	
		@Mock
		ResourceResolver resourceResolver;			
			
		@SuppressWarnings("static-access")
		@Test
		public void test() throws IllegalArgumentException, LoginException
		{
			Map<String, Object> authMap = new HashMap<>();
			authMap.put(ResourceResolverFactory.SUBSERVICE, BEIOConstants.BEIO_ADMIN);
			when(resourceResolverFactory.getServiceResourceResolver(authMap)).thenReturn(resourceResolver);
			try {
				BEIOAutoClosingResourceResolver r = autoClosingResourceResolverFactoryImpl.getResourceResolver(BEIOConstants.BEIO_ADMIN);
			} finally {
				BEIOAutoClosingResourceResolver r1 = autoClosingResourceResolverFactoryImpl.getResourceResolver(authMap);
			}								
		}	
}
