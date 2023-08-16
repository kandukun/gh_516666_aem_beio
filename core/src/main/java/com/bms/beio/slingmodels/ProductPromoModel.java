package com.bms.beio.slingmodels;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables=Resource.class,defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = { "json" })
public class ProductPromoModel extends BaseModel{

	@Inject
	String brandName;
	
	@Inject
	String coverimage;
	
	@Inject
	String subTitle;
	
	@Inject
	String detailedContent;
	
	@Inject 
	String attachmenttext1;
	
	@Inject 
	String attachmentpath1;
	
	@Inject 
	String attachmenttext2;
	
	@Inject 
	String attachmentpath2;
	
	@Inject 
	String attachmenttext3;
	
	@Inject 
	String attachmentpath3;
	
	@Inject 
	String attachmenttext4;
	
	@Inject 
	String attachmentpath4;
	
	@Inject 
	String attachmenttext5;
	
	@Inject 
	String attachmentpath5;
	
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
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

	public String getAttachmenttext1() {
		if(StringUtils.isBlank(attachmenttext1)) {
			return CommonUtils.getAttachmentName(attachmentpath1);
		}
		return attachmenttext1;
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

	public String getAttachmenttext3() {
		if(StringUtils.isBlank(attachmenttext3)) {
			return CommonUtils.getAttachmentName(attachmentpath3);
		}
		return attachmenttext3;
	}

	public void setAttachmenttext3(String attachmenttext3) {
		this.attachmenttext3 = attachmenttext3;
	}

	public String getAttachmentpath3() {
		return attachmentpath3;
	}

	public void setAttachmentpath3(String attachmentpath3) {
		this.attachmentpath3 = attachmentpath3;
	}

	public String getAttachmenttext4() {
		if(StringUtils.isBlank(attachmenttext4)) {
			return CommonUtils.getAttachmentName(attachmentpath4);
		}
		return attachmenttext4;
	}

	public void setAttachmenttext4(String attachmenttext4) {
		this.attachmenttext4 = attachmenttext4;
	}

	public String getAttachmentpath4() {
		return attachmentpath4;
	}

	public void setAttachmentpath4(String attachmentpath4) {
		this.attachmentpath4 = attachmentpath4;
	}

	public String getAttachmenttext5() {
		if(StringUtils.isBlank(attachmenttext5)) {
			return CommonUtils.getAttachmentName(attachmentpath5);
		}
		return attachmenttext5;
	}

	public void setAttachmenttext5(String attachmenttext5) {
		this.attachmenttext5 = attachmenttext5;
	}

	public String getAttachmentpath5() {
		return attachmentpath5;
	}

	public void setAttachmentpath5(String attachmentpath5) {
		this.attachmentpath5 = attachmentpath5;
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
