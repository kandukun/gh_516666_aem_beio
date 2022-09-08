package com.bms.beio.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.config.BmsBeioConfig;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.service.AWSLambdaConnectionService;

@Component(service = AWSLambdaConnectionService.class,immediate=true)
public class AWSLambdaConnectionServiceImpl implements AWSLambdaConnectionService{
	
	Logger LOG = LoggerFactory.getLogger(AWSLambdaConnectionService.class);

	@Reference
	private BmsBeioConfig bmsBeioConfig;
	
	/**
	 * @param path
	 * @param fragmentsJson
	 * This method will connect to AWS service and read the response from it.
	 * @return response status code
	 */
	@Override
	public String sendPushNotificationDetails(String path, JSONObject fragmentsJson) {
		LOG.debug(":::::::::: Entered sendPushNotificationDetails Method of AWSLambdaConnectionService ::::::::::");
		
		HttpURLConnection urlConnection = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		OutputStreamWriter outputWriter = null;
		String awsConnectUserName = bmsBeioConfig.getPushNotificationUsername();
		String awsConnectPassword = bmsBeioConfig.getPushNotificationPassword();
		String statusCode = StringUtils.EMPTY;
		String message = StringUtils.EMPTY;
		
		if(StringUtils.isNotBlank(awsConnectUserName) && StringUtils.isNotBlank(awsConnectPassword) && StringUtils.isNotBlank(bmsBeioConfig.getAWSLambdaServiceURL())) {
			try{
				String authString = awsConnectUserName + BEIOConstants.COLON + awsConnectPassword;
				byte[] authEncBytes = Base64.encodeBase64(authString.getBytes(BEIOConstants.UTF8));
				String authStringEnc = new String(authEncBytes);
				for(int i=0; i<=3; i++){
					if(i>0){
						Thread.sleep(5000);
					}
					try{
						LOG.info("::::::: Push Notification URL being hit is ::::::: " + bmsBeioConfig.getAWSLambdaServiceURL());
						URL object=new URL(bmsBeioConfig.getAWSLambdaServiceURL());
						urlConnection = (HttpURLConnection) object.openConnection();
						urlConnection.setDoOutput(true);
						urlConnection.setDoInput(true);
						urlConnection.setRequestProperty(BEIOConstants.AUTHORIZATION, BEIOConstants.BASIC.concat(BEIOConstants.BLANKSPACE) + authStringEnc);
						urlConnection.setRequestMethod(BEIOConstants.POST); 
						urlConnection.setRequestProperty(BEIOConstants.ACCEPT, BEIOConstants.APPLICATION_JSON);
						urlConnection.setRequestProperty(BEIOConstants.CONTENT_TYPE, "application/json; charset=UTF-8");
						outputWriter = new OutputStreamWriter(urlConnection.getOutputStream(),BEIOConstants.UTF8);
						outputWriter.write(fragmentsJson.toString());
						outputWriter.close();
						inputStream = urlConnection.getInputStream();
						if(null!=inputStream) {
							inputStreamReader = new InputStreamReader(inputStream);
						}
						break;
					}catch(Exception e){
						LOG.error(":::::::::: Exception in  attempt "+ i +" to fetch Temp Creds :::::::::: ",e);
					}
				}
				if(null!=inputStreamReader) {
					int numCharsRead;
					char[] charArray = new char[1024];
					StringBuffer sb = new StringBuffer();
					while ((numCharsRead = inputStreamReader.read(charArray)) > 0) {
						sb.append(charArray, 0, numCharsRead);
					}
					String result = sb.toString();
					LOG.info(":::::::::: Response in service class:::::::::: "+result);
					if(StringUtils.isNotBlank(result)) {
						JSONObject tokenObj = new JSONObject(result);
						statusCode = tokenObj.get(BEIOConstants.STATUS).toString();
						message = tokenObj.get(BEIOConstants.MESSAGE).toString();
					}
				}
				LOG.info(":::::::::: Push Notification Response Message :::::::::: " + message);
			}catch(JSONException e) {
				LOG.error(":::::::::: JSON Exception in attempting to send push notification ::::::::::",e);
			}catch(IOException e) {
				LOG.error(":::::::::: IO Exception in attempting to send push notification ::::::::::",e);
			}catch(InterruptedException e) {
				LOG.error(":::::::::: InterruptedException in attempting to resend send push notification ::::::::::",e);
			}
			finally{
				if(null!=inputStreamReader){
					try{
						inputStreamReader.close();
					}
					catch(IOException e){
						LOG.error(":::::::::: Error in finally block while closing buffered reader :::::::::: ",e);
					}
				}
				if(null!=outputWriter) {
					try {
						outputWriter.close();
					}catch(IOException ex) {
						LOG.error(":::::::::: Error in finally block while closing outputWriter :::::::::: ",ex);
					}
				}
				if(null!=urlConnection) {
					urlConnection.disconnect();
					urlConnection = null;
				}
			}
		}
		LOG.debug(":::::::::: Exiting sendPushNotificationDetails Method of AWSLambdaConnectionService. Status Code from Response is " + statusCode + " ::::::::::");
		return statusCode;
	}
}
