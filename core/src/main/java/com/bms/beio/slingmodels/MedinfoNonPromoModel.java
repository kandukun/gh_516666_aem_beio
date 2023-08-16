package com.bms.beio.slingmodels;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Model(adaptables=Resource.class,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = { "json" })
public class MedinfoNonPromoModel extends BaseModel{
		
	@Inject
	String title;
	
	@Inject
	String coverimage;
	
	@Inject
	String subTitle;
	
	@Inject
	String keyword;
	
	@Inject
	String disclaimer;
	
	@Inject
	String detailedContent;
	
	@Inject
	String compound;
	
	@Inject
	String category;
	
	@Inject 
	String attachmenttext1;
	
	@Inject 
	String attachmentpath1;
	
	@Inject 
	String attachmenttext2;
	
	@Inject 
	String attachmentpath2;
		
	@Inject 
	String videoid;
	
	@Inject
	Integer priority;
	
	@Inject
	List<String> therapeuticarea;
	
	@Inject
	List<String> indication;
		
	@Inject
	List<String> products;
	
	@Inject
	String downloadable;
	
	@Inject
	String shareable;
	
	@Inject
	String type;

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCoverimage() {
		return coverimage;
	}

	public void setCoverimage(String coverimage) {
		this.coverimage = coverimage;
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
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getCompound() {
		return compound;
	}

	public void setCompound(String compound) {
		this.compound = compound;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getAttachmenttext1() {
		if(StringUtils.isBlank(attachmenttext1)) {
			return CommonUtils.getAttachmentName(attachmentpath1);
		}
		return attachmenttext1;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAttachmenttext1(String attachmenttext1) {
		this.attachmenttext1 = attachmenttext1;
	}

	public String getAttachmentpath1() {
		return attachmentpath1;
	}

	public void setAttachmentpath1(String attachmentpath1) {
		this.attachmentpath1 = attachmentpath1;
	}

	public String getAttachmenttext2() {
		if(StringUtils.isBlank(attachmenttext2)) {
			return CommonUtils.getAttachmentName(attachmentpath2);
		}
		return attachmenttext2;
	}

	public void setAttachmenttext2(String attachmenttext2) {
		this.attachmenttext2 = attachmenttext2;
	}

	public String getAttachmentpath2() {
		return attachmentpath2;
	}

	public void setAttachmentpath2(String attachmentpath2) {
		this.attachmentpath2 = attachmentpath2;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<String> getTherapeuticarea() {
		return CommonUtils.getTagList(therapeuticarea, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	@JsonIgnore
	public List<String> getTherapeuticareaTagNames() {
		return CommonUtils.getTagNameList(therapeuticarea, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setTherapeuticarea(List<String> therapeuticarea) {
		this.therapeuticarea = therapeuticarea;
	}

	public List<String> getIndication() {
		return CommonUtils.getTagList(indication, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	@JsonIgnore
	public List<String> getIndicationTagNames() {
		return CommonUtils.getTagNameList(indication, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setIndication(List<String> indication) {
		this.indication = indication;
	}

	public List<String> getTags() {
		return CommonUtils.getTagList(tags, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getProducts() {
		return CommonUtils.getTagList(products, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}
	
	@JsonIgnore
	public List<String> getProductsTagNames() {
		return CommonUtils.getTagNameList(products, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}
	
	public String getDownloadable() {
		return downloadable;
	}

	public void setDownloadable(String downloadable) {
		this.downloadable = downloadable;
	}

	public String getShareable() {
		return shareable;
	}

	public void setShareable(String shareable) {
		this.shareable = shareable;
	}
	
}
