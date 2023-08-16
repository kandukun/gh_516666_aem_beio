package com.bms.beio.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.jcr.LoginException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.BaseModel;
import com.bms.beio.slingmodels.MedinfoNonPromoModel;
import com.bms.beio.slingmodels.MedinfoPromoModel;
import com.bms.beio.slingmodels.NewsModel;
//import com.bms.beio.slingmodels.ProductModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

public class BEIOUtils {
	
	static Logger LOG = LoggerFactory.getLogger(BEIOUtils.class);
	
	public static Predicate<NewsModel>  getNewsPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory) {
		LOG.debug(" ::::::: Entered getNewsPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<NewsModel> newsPredicate = news ->{
			return filterModels(autoClosingFactory,alignmentData,news);
		};
		LOG.debug(" ::::::: Exit from getNewsPredicate Method of BEIOUtils Class ::::::: ");
		return newsPredicate;
	}
	
	/*public static Predicate<ProductModel> getProductsPredicate(List<String> alignmentData, AutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getProductsPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<ProductModel> productsPredicate = product -> { 
			return filterModels(autoClosingFactory,alignmentData,product);
		};
		LOG.debug(" ::::::: Exit from getProductsPredicate Method of BEIOUtils Class ::::::: ");
		return productsPredicate;
	}*/
	
	public static Predicate<ProductPromoModel> getProductsPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getProductsPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<ProductPromoModel> productsPromoPredicate = product -> {
			return filterModels(autoClosingFactory,alignmentData,product);
		};
		LOG.debug(" ::::::: Exit from getProductsPredicate Method of BEIOUtils Class ::::::: ");
		return productsPromoPredicate;
	}
	
	public static Predicate<BEIONonPromoModel> getBEIONonPromoGenericPredicate(List<String> alignmentData, String type){
		LOG.debug(" ::::::: Entered getBEIONonPromoGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)){
			Predicate<BEIONonPromoModel> beioNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIONonPromoGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return beioNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)){
			Predicate<BEIONonPromoModel> beioNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIONonPromoGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return beioNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)){
			Predicate<BEIONonPromoModel> beioNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIONonPromoGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return beioNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)){
			Predicate<BEIONonPromoModel> beioNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIONonPromoGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return beioNonPromoPredicate;
		}else{
			Predicate<BEIONonPromoModel> beioNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIONonPromoGenericPredicate Method of BEIOUtils Class Returned Tags Predicate::::::: ");
			return beioNonPromoPredicate;
		}
	}
	
	public static Predicate<BEIOPromoModel> getBEIOPromoGenericPredicate(List<String> alignmentData, String type){
		LOG.debug(" ::::::: Entered getBEIOPromoGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)){
			Predicate<BEIOPromoModel> beioPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIOPromoGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return beioPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)){
			Predicate<BEIOPromoModel> beioPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIOPromoGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return beioPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)){
			Predicate<BEIOPromoModel> beioPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIOPromoGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return beioPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)){
			Predicate<BEIOPromoModel> beioPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIOPromoGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return beioPromoPredicate;
		}else{
			Predicate<BEIOPromoModel> beioPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTagNames());
			};
			LOG.debug(" ::::::: Exit from getBEIOPromoGenericPredicate Method of BEIOUtils Class Returned TAGS Predicate::::::: ");
			return beioPromoPredicate;
		}
	}
	
	public static Predicate<ProductPromoModel> getProductsPromoGenericPredicate(List<String> alignmentData,String type){
		LOG.debug(" ::::::: Entered getProductsGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)) {
			Predicate<ProductPromoModel> productsPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return productsPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)){
			Predicate<ProductPromoModel> productsPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return productsPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)) {
			Predicate<ProductPromoModel> productsPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return productsPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)){
			Predicate<ProductPromoModel> productsPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return productsPromoPredicate;
		}else {
			Predicate<ProductPromoModel> productsPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsGenericPredicate Method of BEIOUtils Class Returned TAGS Predicate::::::: ");
			return productsPromoPredicate;
		}
	}
	
	public static Predicate<ProductNonPromoModel> getProductsNonPromoGenericPredicate(List<String> alignmentData,String type){
		LOG.debug(" ::::::: Entered getProductsNonPromoGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)) {
			Predicate<ProductNonPromoModel> productsNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsNonPromoGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return productsNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)) {
			Predicate<ProductNonPromoModel> productsNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsNonPromoGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return productsNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)) {
			Predicate<ProductNonPromoModel> productsNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsNonPromoGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return productsNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)) {
			Predicate<ProductNonPromoModel> productsNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsNonPromoGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return productsNonPromoPredicate;
		}else {
			Predicate<ProductNonPromoModel> productsNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getProductsNonPromoGenericPredicate Method of BEIOUtils Class Returned TAGS Predicate::::::: ");
			return productsNonPromoPredicate;
		}
	}
	
	public static Predicate<ProductNonPromoModel> getProductsNonPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getProductsPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<ProductNonPromoModel> productsNonPromoPredicate = product -> {
			return filterModels(autoClosingFactory,alignmentData,product);
		};
		LOG.debug(" ::::::: Exit from getProductsPredicate Method of BEIOUtils Class ::::::: ");
		return productsNonPromoPredicate;
	}
	
	public static Predicate<BEIOPromoModel> getBEIOPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getBEIOPromoPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<BEIOPromoModel> beioPromoPredicate = promo->{
			return filterModels(autoClosingFactory, alignmentData, promo);
		};
		LOG.debug(" ::::::: Exit from getBEIOPromoPredicate Method of BEIOUtils Class ::::::: ");
		return beioPromoPredicate;
	}
	
	public static Predicate<BEIONonPromoModel> getBEIONonPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getBEIONonPromoPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<BEIONonPromoModel> beioNonPromoPredicate = nonpromo->{
			return filterModels(autoClosingFactory, alignmentData, nonpromo);
		};
		LOG.debug(" ::::::: Exit from getBEIONonPromoPredicate Method of BEIOUtils Class ::::::: ");
		return beioNonPromoPredicate;
	}
	public static Predicate<MedinfoPromoModel> getMedinfoPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getMedinfoPromoPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<MedinfoPromoModel> medinfoPromoPredicate = promo->{
			return filterModels(autoClosingFactory, alignmentData, promo);
		};
		LOG.debug(" ::::::: Exit from getMedinfoPromoPredicate Method of BEIOUtils Class ::::::: ");
		return medinfoPromoPredicate;
	}
	public static Predicate<MedinfoNonPromoModel> getMedinfoNonPromoPredicate(List<String> alignmentData, BEIOAutoClosingResourceResolverFactory autoClosingFactory){
		LOG.debug(" ::::::: Entered getMedinfoNonPromoPredicate Method of BEIOUtils Class ::::::: ");
		Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = promo->{
			return filterModels(autoClosingFactory, alignmentData, promo);
		};
		LOG.debug(" ::::::: Exit from getMedinfoNonPromoPredicate Method of BEIOUtils Class ::::::: ");
		return medinfoNonPromoPredicate;
	}
	private static boolean listCheck(List<String> alignmentData, List<String> fragmentTags) {
		LOG.debug(" ::::::: Entered listCheck Method of BEIOUtils Class ::::::: ");
		if(null==alignmentData && null==fragmentTags) {
			return true;
		}
		if(null==alignmentData || alignmentData.size()==0) {
			return true;
		}
		if(null==fragmentTags || fragmentTags.size()==0) {
			return false;
		}
		if(alignmentData.size()>0 && fragmentTags.size()>0) {
			return alignmentData.parallelStream().filter(fragmentTags::contains).collect(Collectors.toList()).size()>0;
		}
		LOG.debug(" ::::::: Exit from listCheck Method of BEIOUtils Class ::::::: ");
		return false;
	}
	
	public static Predicate<MedinfoPromoModel> getMedinfoPromoGenericPredicate(List<String> alignmentData, String type){
		LOG.debug(" ::::::: Entered getMedinfoPromoGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)){
			Predicate<MedinfoPromoModel> medinfoPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoPromoGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return medinfoPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)){
			Predicate<MedinfoPromoModel> medinfoPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoPromoGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return medinfoPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)){
			Predicate<MedinfoPromoModel> medinfoPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoPromoGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return medinfoPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)){
			Predicate<MedinfoPromoModel> medinfoPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoPromoGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return medinfoPromoPredicate;
		}else{
			Predicate<MedinfoPromoModel> medinfoPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoPromoGenericPredicate Method of BEIOUtils Class Returned TAGS Predicate::::::: ");
			return medinfoPromoPredicate;
		}
	}
	public static Predicate<MedinfoNonPromoModel> getMedinfoNonPromoGenericPredicate(List<String> alignmentData, String type){
		LOG.debug(" ::::::: Entered getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class ::::::: ");
		if(StringUtils.equalsIgnoreCase(type, BEIOConstants.ROLE)){
			Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getRoleTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class Returned ROLE Predicate::::::: ");
			return medinfoNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.THERUPATIC_AREA)){
			Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTherapeuticareaTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class Returned THERUPATIC_AREA Predicate::::::: ");
			return medinfoNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.INDICATION)){
			Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getIndicationTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class Returned INDICATION Predicate::::::: ");
			return medinfoNonPromoPredicate;
		}else if(StringUtils.equalsIgnoreCase(type, BEIOConstants.PRODUCTS)){
			Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getProductsTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class Returned PRODUCTS Predicate::::::: ");
			return medinfoNonPromoPredicate;
		}else{
			Predicate<MedinfoNonPromoModel> medinfoNonPromoPredicate = product ->{
				return listCheck(alignmentData, product.getTagNames());
			};
			LOG.debug(" ::::::: Exit from getMedinfoNonPromoGenericPredicate Method of BEIOUtils Class Returned TAGS Predicate::::::: ");
			return medinfoNonPromoPredicate;
		}
	}
	private static boolean filterModels(BEIOAutoClosingResourceResolverFactory autoClosingFactory, List<String> alignmentData, BaseModel model) {
		LOG.debug(" ::::::: Entered filterModels Method of BEIOUtils Class ::::::: ");
		try(BEIOAutoClosingResourceResolver autoClosingResourceResolver = autoClosingFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			ResourceResolver resourceResolver = autoClosingResourceResolver.getResurceResolver();
			if(null!=resourceResolver) {
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				if(null!=tagManager) {
					Optional<List<String>> tagsNullchk = Optional.ofNullable(model.getRawRole());
					if(tagsNullchk.isPresent() && null!= tagsNullchk.get()) {
						for(String rawTag:tagsNullchk.get()) {
							Tag tag = tagManager.resolve(rawTag);
							if(null!=tag) {
								for(String alignment : alignmentData) {
									if(StringUtils.equals(alignment, tag.getName())) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		} catch(IllegalArgumentException | LoginException e) {
			LOG.error(" ::::::: Exception occured while filtering tags in filterModels Method of BEIOUtils Class ::::::: " , e);
		} catch(Exception e) {
			LOG.error(" ::::::: Broad Exception occured while filtering tags in filterModels Method of BEIOUtils Class ::::::: " , e);
		}
		LOG.debug(" ::::::: Exit from filterModels Method of BEIOUtils Class ::::::: ");
		return false;
	}

}
