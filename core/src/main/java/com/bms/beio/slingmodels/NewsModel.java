package com.bms.beio.slingmodels;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author marri.shashanka
 *
 */
@Model(adaptables=Resource.class,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = { "json" })
public class NewsModel extends BaseModel{

	Logger LOG = LoggerFactory.getLogger(NewsModel.class);

	@Inject @Required
	private String title;

	@Inject
	private String coverimage;

	@Inject
	private String subTitle;

	@Inject
	private String detailedContent;

	@Inject
	private String attachmenttext;

	@Inject
	private String attachmentpath;

	public String getCoverimage() {
		return coverimage;
	}

	public void setCoverimage(String coverimage) {
		this.coverimage = coverimage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getCfUniqueID() {
		return cfUniqueID;
	}

	public void setCfUniqueID(String cfUniqueID) {
		this.cfUniqueID = cfUniqueID;
	}

	@JsonIgnore
	public String getSortingDate() {
		if(StringUtils.isNotBlank(publishDate)){
			return publishDate;
		}else {
			return creationDate;
		}
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getDetailedContent() {
		return detailedContent;
	}

	public void setDetailedContent(String detailedContent) {
		this.detailedContent = detailedContent;
	}

	public String getAttachmenttext() {
		return attachmenttext;
	}

	public void setAttachmenttext(String attachmenttext) {
		this.attachmenttext = attachmenttext;
	}

	public String getAttachmentpath() {
		return attachmentpath;
	}

	public void setAttachmentpath(String attachmentpath) {
		this.attachmentpath = attachmentpath;
	}

}
