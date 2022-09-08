package com.bms.beio.service.impl;

import static org.mockito.Mockito.when;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bms.beio.config.BmsBeioConfig;

@RunWith(PowerMockRunner.class)
public class AWSLambdaConnectionServiceImplTest {
	
	@InjectMocks
	AWSLambdaConnectionServiceImpl awslambaconnectionserviceimpl;
	
	@Mock
	BmsBeioConfig bmsBeioConfig;
	
	@Mock
	JSONObject fragmentsJson;
	
	@Test
	public void test()
	{	//awslambaconnectionserviceimpl.activate();
		awslambaconnectionserviceimpl.sendPushNotificationDetails("/content/beio/us/en_us/home/products", fragmentsJson);
	}
	
	@Test
	public void testCatchBlock()
	{
		when(bmsBeioConfig.getAWSLambdaServiceURL()).thenReturn(null);
		awslambaconnectionserviceimpl.sendPushNotificationDetails("/content/beio/us/en_us/home/products", fragmentsJson);
	}
}
