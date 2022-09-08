package com.bms.beio.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.bms.beio.config.BmsBeioConfig.Configuration;

public class BmsBeioConfigTest {

	@Mock
	Configuration config;
	
	BmsBeioConfig bmsBeioConfig=new BmsBeioConfig();
	String[] replicationAgents= {"",""};
	
	@Before
	public void setUp()
	{
		config=mock(Configuration.class);
		when(config.contentRoot()).thenReturn("/content/config/contentroot");
		when(config.damRoot()).thenReturn("/content/config/damroot");
		when(config.replicationAgents()).thenReturn(replicationAgents);
		bmsBeioConfig.activate(config);
	}
	
	@Test
	public void test()
	{
		Assert.assertNotNull(bmsBeioConfig);
		Assert.assertEquals("/content/config/contentroot",bmsBeioConfig.getContentRoot());
		Assert.assertEquals("/content/config/damroot", bmsBeioConfig.getDamRoot());
		Assert.assertArrayEquals(replicationAgents,bmsBeioConfig.getReplicationAgents());
		Assert.assertEquals(null, bmsBeioConfig.getAWSLambdaServiceURL());
	}
}
