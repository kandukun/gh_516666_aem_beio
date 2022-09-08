package com.bms.beio.resource;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.AutoClosingResourceResolverUtil;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;



public class AutoClosingResourceResolverUtilTest{
	
	@Mock
	ResourceResolverFactory resourceResolverFactory;
	@Mock
	ResourceResolver resourceResolver;
	@Mock
	Session session;
	@Mock
	String serviceUser = "servideUser";
	@Mock
	BEIOAutoClosingResourceResolver autoClosingResourceResolver;
/*	@Mock
	AutoClosingResourceResolverImpl autoClosingResourceResolverImpl;*/
	
	AutoClosingResourceResolverUtil autoClosingResourceResolverUtil = new AutoClosingResourceResolverUtil();
	Map<String, Object> authMap = new HashMap<>();
	@Before
	public void setUp() throws IllegalArgumentException, LoginException {
		
		authMap.put(ResourceResolverFactory.SUBSERVICE, BEIOConstants.BEIO_ADMIN);
		
		resourceResolverFactory = mock(ResourceResolverFactory.class);
		session = mock(Session.class);
		resourceResolver = mock(ResourceResolver.class);
		autoClosingResourceResolver = mock(BEIOAutoClosingResourceResolver.class);
		when(resourceResolver.isLive()).thenReturn(true);
		when(resourceResolverFactory.getServiceResourceResolver(authMap)).thenReturn(resourceResolver);
		when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
	}
		
	@Test
	public void testInnerClass() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, NoSuchMethodException
	{
		Class<?> autoClosingResourceResolverImpl=AutoClosingResourceResolverUtil.class.getDeclaredClasses()[0];
		Constructor<?> constructor=autoClosingResourceResolverImpl.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		Object innerclassobject=constructor.newInstance(resourceResolver);
		java.lang.reflect.Field field= autoClosingResourceResolverImpl.getDeclaredField("resourceResolver");
		field.setAccessible(true);		
		Method method=autoClosingResourceResolverImpl.getDeclaredMethod("getResurceResolver", new Class<?>[]{});
		method.setAccessible(true);
		method.invoke(innerclassobject, new Object[]{});
		Method method1=autoClosingResourceResolverImpl.getDeclaredMethod("getSession", new Class<?>[]{});
		method1.setAccessible(true);
		method1.invoke(innerclassobject, new Object[]{});
		Method method2=autoClosingResourceResolverImpl.getDeclaredMethod("close", new Class<?>[]{});
		method2.setAccessible(true);
		method2.invoke(innerclassobject, new Object[]{});	
		//{sling.service.subservice=beioadmin}
	}
	@Test
	public void test() throws IllegalArgumentException, LoginException, InstantiationException, IllegalAccessException, InvocationTargetException {
		//autoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory, "autmap");
		
		try {
			autoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory, BEIOConstants.BEIO_ADMIN);
			try
			{
			autoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory, "");
			}
			catch(IllegalArgumentException e) {}
		} finally {	
		authMap=null;
		try {
		autoClosingResourceResolverUtil.getResourceResolver(resourceResolverFactory, authMap);}
		catch(IllegalArgumentException e) {}
		}
	}

}
