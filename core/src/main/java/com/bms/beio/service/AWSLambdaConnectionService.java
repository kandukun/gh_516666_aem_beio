package com.bms.beio.service;

import org.json.JSONObject;

public interface AWSLambdaConnectionService {

	public String sendPushNotificationDetails(String path, JSONObject fragmentsJson);
}
