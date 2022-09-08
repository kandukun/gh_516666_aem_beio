package com.bms.beio.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.LoginException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.MedinfoNonPromoModel;
import com.bms.beio.slingmodels.MedinfoPromoModel;
import com.bms.beio.slingmodels.NewsModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.beio.utils.BEIOUtils;
import com.bms.beio.utils.CommonUtils;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component(service=Servlet.class,property= {
		ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES+"=beio-mobile-app/components/page/beio-generic-listing-page",
		ServletResolverConstants.SLING_SERVLET_METHODS+"=GET",
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS+"=mobile",
		ServletResolverConstants.SLING_SERVLET_SELECTORS+"=json"
},immediate=true)
public class BEIOGenericListingServlet extends SlingSafeMethodsServlet{
	
	Logger LOG = LoggerFactory.getLogger(BEIOGenericListingServlet.class);
	
	@Reference
	BMSDomainFactory bmsDomainFactory;
	
	@Reference
	BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug(" ::::::: Entered doGet Method of BEIOGenericListingServlet ::::::: ");
		//https://beio.bms.com/us/en/onc/news.ffc_10-020-091.role_prim.checkdate.json.mobile
		//https://beio.bms.com/us/en/onc/io.ffc_10-020-091.role_prim.checkdate.json.mobile
		//https://beio.bms.com/us/en/onc/news.ffc_10-020-091.role_prim.checkdate.json.mobile
		
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonResponse =  mapper.createObjectNode();
			String path = request.getRequestPathInfo().getResourcePath().concat(BEIOConstants.SLASH).concat(BEIOConstants.ROOT);
			String[] selectors = request.getRequestPathInfo().getSelectors();
			List<String> requestAttributes = new ArrayList<>();
			boolean checkdateFlag = false; 
			checkdateFlag = Arrays.asList(selectors).contains(BEIOConstants.CHECK_DATE);
			LOG.debug(" ::::::: checkdateFlag:_::::::: "+checkdateFlag);
			if(!checkdateFlag) {
				Predicate<String> stripExtensions = s->{return !(StringUtils.equalsIgnoreCase(s,BEIOConstants.MOBILE) || StringUtils.equalsIgnoreCase(s,BEIOConstants.JSON) || StringUtils.equalsIgnoreCase(s,BEIOConstants.FULL)); };
				requestAttributes = Arrays.asList(selectors).parallelStream().filter(stripExtensions).collect(Collectors.toList());
			}
			if(checkdateFlag) {
				Predicate<String> stripExtensions = s->{return !(StringUtils.equalsIgnoreCase(s,BEIOConstants.MOBILE) || StringUtils.equalsIgnoreCase(s,BEIOConstants.JSON) || StringUtils.equalsIgnoreCase(s,BEIOConstants.CHECK_DATE) || StringUtils.equalsIgnoreCase(s,BEIOConstants.FULL));};
				requestAttributes = Arrays.asList(selectors).parallelStream().filter(stripExtensions).collect(Collectors.toList());
			}
			final List<String> alignmentData = requestAttributes;
			if(StringUtils.isNotBlank(path) && CommonUtils.checkResource(resourceResolver.resolve(path))) {
				Resource root = resourceResolver.resolve(path);
				Iterable<Resource> children = root.getChildren(); 
				for(Resource resource : children){
					
					if(StringUtils.equalsIgnoreCase(resource.getResourceType(), BEIOConstants.BEIO_GENERIC_LISTING_TEMPLATE)) {
						Optional<Resource> resourceNullchk = Optional.of(resource);
						if(resourceNullchk.isPresent()) {
							GenericListingComponentModel genericListingModel = resource.adaptTo(GenericListingComponentModel.class);
							if(null!= genericListingModel && StringUtils.isNotBlank(genericListingModel.getFormtype()) &&  (StringUtils.isNotBlank(genericListingModel.getPromosourcepath()) || StringUtils.isNotBlank(genericListingModel.getNonpromosourcepath()))) {
								switch(genericListingModel.getFormtype()) {
									case BEIOConstants.PRODUCTS:
										constructProductsList(genericListingModel, alignmentData, autoClosingResourceResolverFactory, jsonResponse, mapper,request,checkdateFlag);
									break;
										
									case BEIOConstants.ALL:
										constructAllList(genericListingModel, alignmentData, autoClosingResourceResolverFactory, jsonResponse, mapper,request,checkdateFlag);
									break;
									
									default:
									return;
								}
							}
						}
					}
				}
			}
			response.setContentType("text/json");
			response.getWriter().write(mapper.writeValueAsString(jsonResponse));
		} catch (LoginException | IllegalArgumentException e) {
			LOG.error(" ::::::: Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
		} catch (Exception e) {
			LOG.error(" ::::::: Broad Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
		}
		LOG.debug(" ::::::: Exit from doGet Method of BEIOGenericListingServlet ::::::: ");
	}
	
	/**
	 * @param genericListingModel
	 * @param alignmentData
	 * @param autoClosingResourceResolverFactory
	 * @param jsonResponse
	 * @param mapper
	 * @param request
	 * @param checkDate
	 */
	private void constructProductsList(GenericListingComponentModel genericListingModel ,List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory, ObjectNode jsonResponse, ObjectMapper mapper, SlingHttpServletRequest request, boolean checkDate) {
		LOG.debug(" ::::::: Entered constructProductsList Method of BEIOGenericListingServlet ::::::: ");
		List<ProductPromoModel> productsPromoList =  genericListingModel.getProductPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			productsPromoList = productsPromoList.parallelStream().filter(BEIOUtils.getProductsPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
		}else {
			productsPromoList = productsPromoList.parallelStream().sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
		}
		productsPromoList.forEach(p->{ 
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			p.setAttachmentpath3(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath3(), p.isPublish(), request));
			p.setAttachmentpath4(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath4(), p.isPublish(), request));
			p.setAttachmentpath5(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath5(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing productsPromoList detailed content in constructProductsList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		List<ProductNonPromoModel> productsNonPromoList = genericListingModel.getProductNonPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			productsNonPromoList = productsNonPromoList.parallelStream().filter(BEIOUtils.getProductsNonPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
		}else {
			productsNonPromoList = productsNonPromoList.parallelStream().sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
		}
		productsNonPromoList.forEach(p->{
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			p.setAttachmentpath3(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath3(), p.isPublish(), request));
			p.setAttachmentpath4(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath4(), p.isPublish(), request));
			p.setAttachmentpath5(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath5(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing products-Non-PromoList detailed content in constructProductsList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		if(checkDate) {
			jsonResponse.set("ProductsPromo CheckDate", mapper.valueToTree(productsPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));
			jsonResponse.set("Products Non Promo CheckDate", mapper.valueToTree(productsNonPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));
		}else {
			jsonResponse.set("ProductsPromo", mapper.valueToTree(productsPromoList));			
			jsonResponse.set("Products Non Promo", mapper.valueToTree(productsNonPromoList));
		}
		LOG.debug(" ::::::: Exit from constructProductsList Method of BEIOGenericListingServlet ::::::: ");
	}
	
	/**
	 * @param genericListingModel
	 * @param alignmentData
	 * @param autoClosingResourceResolverFactory
	 * @param jsonResponse
	 * @param mapper
	 * @param request
	 * @param checkDate
	 */
	private void constructAllList(GenericListingComponentModel genericListingModel ,List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory, ObjectNode jsonResponse, ObjectMapper mapper,SlingHttpServletRequest request, boolean checkDate) {
		LOG.debug(" ::::::: Entered constructAllList Method of BEIOGenericListingServlet ::::::: ");
		List<BEIOPromoModel> beioPromoList = genericListingModel.getBEIOPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			beioPromoList = beioPromoList.parallelStream().filter(BEIOUtils.getBEIOPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).collect(Collectors.toList());
		}else {
			beioPromoList = beioPromoList.parallelStream().collect(Collectors.toList());
		}
		beioPromoList.forEach(p->{
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			p.setAttachmentpath3(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath3(), p.isPublish(), request));
			p.setAttachmentpath4(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath4(), p.isPublish(), request));
			p.setAttachmentpath5(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath5(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing beioPromoList in constructAllList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		List<BEIONonPromoModel> beioNonPromoList = genericListingModel.getBEIONonPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			beioNonPromoList = beioNonPromoList.parallelStream().filter(BEIOUtils.getBEIONonPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).collect(Collectors.toList());
		}else {
			beioNonPromoList = beioNonPromoList.parallelStream().collect(Collectors.toList());
		}
		beioNonPromoList.forEach(p->{
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			p.setAttachmentpath3(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath3(), p.isPublish(), request));
			p.setAttachmentpath4(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath4(), p.isPublish(), request));
			p.setAttachmentpath5(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath5(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing beioNonPromoList in constructAllList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		List<MedinfoPromoModel> medinfoPromoList = genericListingModel.getMedinfoPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			medinfoPromoList = medinfoPromoList.parallelStream().filter(BEIOUtils.getMedinfoPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).collect(Collectors.toList());
		}else {
			medinfoPromoList = medinfoPromoList.parallelStream().collect(Collectors.toList());
		}	
		medinfoPromoList.forEach(p->{
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing MedinfoPromoList in constructAllList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		
		List<MedinfoNonPromoModel> medinfoNonPromoList = genericListingModel.getMedinfoNonPromoList();
		if(null!=alignmentData && alignmentData.size()>0) {
			medinfoNonPromoList = medinfoNonPromoList.parallelStream().filter(BEIOUtils.getMedinfoNonPromoPredicate(alignmentData, autoClosingResourceResolverFactory)).collect(Collectors.toList());
		}else {
			medinfoNonPromoList = medinfoNonPromoList.parallelStream().collect(Collectors.toList());
		}	
		medinfoNonPromoList.forEach(p->{
			p.setCoverimage(bmsDomainFactory.getEndUserExternalLink(p.getCoverimage(), p.isPublish(), request));
			p.setAttachmentpath1(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath1(), p.isPublish(), request));
			p.setAttachmentpath2(bmsDomainFactory.getEndUserExternalLink(p.getAttachmentpath2(), p.isPublish(), request));
			try {
				p.setDetailedContent(CommonUtils.getHrefTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
				p.setDetailedContent(CommonUtils.getImgTextValue(p.getDetailedContent(), null, bmsDomainFactory, p.isPublish(), request));
			}catch(URISyntaxException ex) {
				LOG.error("  ::::::: Exception occured while processing MedinfoNonPromoList in constructAllList Method of BEIOGenericListingServlet ::::::: " , ex);
			}
		});
		if(checkDate) {
			jsonResponse.set("BEIO Promo List CheckDate", mapper.valueToTree(beioPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));
			jsonResponse.set("BEIO Non Promo List CheckDate" , mapper.valueToTree(beioNonPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));
			jsonResponse.set("Medinfo Promo List CheckDate" , mapper.valueToTree(medinfoPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));
			jsonResponse.set("Medinfo Non Promo List CheckDate" , mapper.valueToTree(medinfoNonPromoList.parallelStream().sorted(CommonUtils::fragmentsDateSorter).collect(Collectors.toList()).get(0).getPublishDate()));

		}else {
			ObjectNode internalPromoResponse = mapper.createObjectNode();
			internalPromoResponse.set("news", mapper.valueToTree(beioPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.NEWS_TYPE);}).sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList())));
			internalPromoResponse.set("ioLectures", mapper.valueToTree(beioPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.IOLECTURES);}).sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList())));
			internalPromoResponse.set("clinicalCases", mapper.valueToTree(beioPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.CLINICALCASES);}).sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList())));
			internalPromoResponse.set("events", mapper.valueToTree(beioPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.EVENTS);}).sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList())));
			internalPromoResponse.set("contacts", mapper.valueToTree(beioPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.CONTACTS);}).sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList())));
			jsonResponse.set("BEIO Promo List", internalPromoResponse);
			
			ObjectNode internalNonPromoResponse = mapper.createObjectNode();
			internalNonPromoResponse.set("news", mapper.valueToTree(beioNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.NEWS_TYPE);}).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList())));
			internalNonPromoResponse.set("ioLectures", mapper.valueToTree(beioNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.IOLECTURES);}).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList())));
			internalNonPromoResponse.set("clinicalCases", mapper.valueToTree(beioNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.CLINICALCASES);}).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList())));
			internalNonPromoResponse.set("events", mapper.valueToTree(beioNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.EVENTS);}).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList())));
			internalNonPromoResponse.set("contacts", mapper.valueToTree(beioNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.CONTACTS);}).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList())));
			jsonResponse.set("BEIO Non Promo List", internalNonPromoResponse);
			
			ObjectNode internalMedinfoPromoResponse = mapper.createObjectNode();
			internalMedinfoPromoResponse.set("medinfo", mapper.valueToTree(medinfoPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.MEDINFO);}).sorted(CommonUtils::medinfoPromoSorter).collect(Collectors.toList())));
			jsonResponse.set("Medinfo Promo List", internalMedinfoPromoResponse);
			
			ObjectNode internalMedinfoNonPromoResponse = mapper.createObjectNode();
			internalMedinfoNonPromoResponse.set("medinfo", mapper.valueToTree(medinfoNonPromoList.parallelStream().filter( p-> { return StringUtils.equalsIgnoreCase(p.getType(), BEIOConstants.MEDINFO);}).sorted(CommonUtils::medinfoNonPromoSorter).collect(Collectors.toList())));
			jsonResponse.set("Medinfo Non Promo List", internalMedinfoNonPromoResponse);
		}
		
		LOG.debug(" ::::::: Exit from constructAllList Method of BEIOGenericListingServlet ::::::: ");
	}
	
}
