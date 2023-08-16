package com.bms.beio.config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@Component(service=BmsBeioConfig.class,immediate=true)
@Designate(ocd= BmsBeioConfig.Configuration.class)
public class BmsBeioConfig {

	private String damRoot;
	private String contentRoot;
	private String[] replicationAgents;
	private String AWSLambdaServiceURL;
	private String pushNotificationUsername;
	private String pushNotificationPassword;
	
	@ObjectClassDefinition(name="BEIO Application Configurations",description=" Configure BEIO Application Base Values ")
	public @interface Configuration{
		
		@AttributeDefinition(name="Dam Root", type=AttributeType.STRING, description="Configure the DAM Root Path")
		String damRoot();
		
		@AttributeDefinition(name="Content Root", type=AttributeType.STRING, description="Configure the Content Root Path")
		String contentRoot();
		
		@AttributeDefinition(name="Replication Agents",type=AttributeType.STRING, description="Configure the Replication Agents")
		String[] replicationAgents();
		
		@AttributeDefinition(name="Lambda Service URL", type=AttributeType.STRING, description="Configure the AWS Lambda Service URL")
		String AWSLambdaServiceURL();
		
		@AttributeDefinition(name="BEIO Push Notification UserName", type=AttributeType.STRING, description="Configure the BEIO Push Notification Username")
		String pushNotificationUsername();
		
		@AttributeDefinition(name="BEIO Push Notification Password", type=AttributeType.PASSWORD, description="Configure the BEIO Push Notification Password")
		String pushNotificationPassword();
	}
	
	@Activate
	public void activate(Configuration config) {
		this.setContentRoot(config.contentRoot());
		this.setDamRoot(config.damRoot());
		this.setReplicationAgents(config.replicationAgents());
		this.setAWSLambdaServiceURL(config.AWSLambdaServiceURL());
		this.setPushNotificationUsername(config.pushNotificationUsername());
		this.setPushNotificationPassword(config.pushNotificationPassword());
	}

	public String getDamRoot() {
		return damRoot;
	}

	public void setDamRoot(String damRoot) {
		this.damRoot = damRoot;
	}

	public String getContentRoot() {
		return contentRoot;
	}

	public void setContentRoot(String contentRoot) {
		this.contentRoot = contentRoot;
	}

	public String[] getReplicationAgents() {
		return replicationAgents;
	}

	public void setReplicationAgents(String[] replicationAgents) {
		this.replicationAgents = replicationAgents;
	}
	
	public String getAWSLambdaServiceURL() {
		return AWSLambdaServiceURL;
	}

	public void setAWSLambdaServiceURL(String AWSLambdaServiceURL) {
		this.AWSLambdaServiceURL = AWSLambdaServiceURL;
	}

	public String getPushNotificationUsername() {
		return pushNotificationUsername;
	}

	public void setPushNotificationUsername(String pushNotificationUsername) {
		this.pushNotificationUsername = pushNotificationUsername;
	}

	public String getPushNotificationPassword() {
		return pushNotificationPassword;
	}

	public void setPushNotificationPassword(String pushNotificationPassword) {
		this.pushNotificationPassword = pushNotificationPassword;
	}
	
}
