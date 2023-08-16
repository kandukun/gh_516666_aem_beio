package com.bms.beio.slingmodels;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.utils.CommonUtils;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.bms.bmscorp.core.utils.BMSResourceResolverUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author marri.shashanka
 *
 */
@Model(adaptables=Resource.class,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = { "json" })
public class BaseModel {

	@Inject
	protected BMSDomainFactory bmsDomainFactory;
	
	@Inject
	protected BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Inject
	protected List<String> role;
	
	@Inject
	protected List<String> tags;
	
	@Inject @JsonIgnore
	protected SlingSettingsService slingService;
	
	@Inject
	protected String publishDate;

	@Inject @JsonIgnore
	protected String creationDate;

	@Inject @Named(value="content_fragment_unique_id") 
	protected String cfUniqueID;
	
	
	public List<String> getRole() {
		return CommonUtils.getTagList(role, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	@JsonIgnore
	public List<String> getRoleTagNames() {
		return CommonUtils.getTagNameList(role, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	public void setRole(List<String> role) {
		this.role = role; 
	}
	
	public List<String> getTags() {
		return CommonUtils.getTagList(tags, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	@JsonIgnore
	public List<String> getTagNames() {
		return CommonUtils.getTagNameList(tags, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	@JsonIgnore
	public List<String> getRawRole() {
		return role;
	}
	
	public SlingSettingsService getSlingService() {
		return slingService;
	}
	
	public void setSlingService(SlingSettingsService slingService) {
		this.slingService = slingService;
	}
	
	public String getPublishDate() {
		return CommonUtils.dateFormat(publishDate);
	}
	
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getCfUniqueID() {
		return cfUniqueID;
	}
	
	public void setCfUniqueID(String cfUniqueID) {
		this.cfUniqueID = cfUniqueID;
	}
	
	@JsonIgnore
	public boolean isPublish() {
		return BMSResourceResolverUtil.isPublish(slingService);
	}
}
