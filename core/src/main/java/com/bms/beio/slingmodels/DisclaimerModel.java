package com.bms.beio.slingmodels;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json")
public class DisclaimerModel {
	
	@Inject
	private String contentType;
	
	@Inject
	private String content;
	
	public String getContentType() {
		return contentType;
	}

	public String getContent() {
		return content;
	}

}
