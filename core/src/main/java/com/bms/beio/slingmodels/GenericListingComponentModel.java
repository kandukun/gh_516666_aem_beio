package com.bms.beio.slingmodels;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.utils.BEIOUtils;
import com.bms.beio.utils.CommonUtils;
import com.bms.beio.utils.ContentFragmentHelper;
import com.bms.beio.utils.QueryBuilder;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.crx.JcrConstants;

@Model(adaptables= {Resource.class}, defaultInjectionStrategy=DefaultInjectionStrategy.OPTIONAL)
@Exporter(name="jackson", extensions = { "json" })
public class GenericListingComponentModel extends BaseModel{
	
	Logger LOG = LoggerFactory.getLogger(GenericListingComponentModel.class);
	
	@Inject
	private BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Inject
	private BMSDomainFactory domainFactory;
	
	@Inject
	private SlingSettingsService slingSettingService;
	
	@Inject
	private String formtype;
	
	@Inject
	private String promosourcepath;
	
	@Inject
	private String nonpromosourcepath;
	
	@Inject
	private List<String> therapeutic;
	
	@Inject
	private List<String> indication;
	
	@Inject
	private List<String> products;
	
	@Inject
	private String downloadable;
	
	@Inject
	private String shareable;
	
	private String syncDate;
	
	private List<String> filterAttributes = new LinkedList<>();
	
	private List<ProductPromoModel> htlProductPromoList;

	private List<ProductNonPromoModel> htlProductNonPromoList;

	private List<BEIOPromoModel> HTLBEIOPromoList;
	
	private List<BEIONonPromoModel> HTLBEIONonPromoList;
	
	private List<NewsModel> htlNewsList;
	
	private List<BEIONonPromoModel> BEIONonPromoList;
	
	private List<NewsModel> newsList; 
	
	private List<MedinfoPromoModel> HTLMedinfoPromoList;
	
	private List<MedinfoNonPromoModel> HTLMedinfoNonPromoList;
	
	public String getFormtype() {
		return formtype;
	}

	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}

	public String getPromosourcepath() {
		return promosourcepath;
	}

	public void setPromosourcepath(String promosourcepath) {
		this.promosourcepath = promosourcepath;
	}

	public String getNonpromosourcepath() {
		return nonpromosourcepath;
	}

	public void setNonpromosourcepath(String nonpromosourcepath) {
		this.nonpromosourcepath = nonpromosourcepath;
	}

	public List<String> getTherapeutic() {
		return CommonUtils.getTagNameList(therapeutic, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setTherapeutic(List<String> therapeutic) {
		this.therapeutic = therapeutic;
	}

	public List<String> getIndication() {
		return CommonUtils.getTagNameList(indication, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setIndication(List<String> indication) {
		this.indication = indication;
	}

	public List<String> getProducts() {
		return CommonUtils.getTagNameList(products, autoClosingResourceResolverFactory, BEIOConstants.BEIO_ADMIN);
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

	public String getDownloadable() {
		if(StringUtils.isBlank(downloadable)) {
			return "false";
		}
		return downloadable;
	}

	public void setDownloadable(String downloadable) {
		this.downloadable = downloadable;
	}

	public String getShareable() {
		if(StringUtils.isBlank(shareable)) {
			return "false";
		}
		return shareable;
	}

	public void setShareable(String shareable) {
		this.shareable = shareable;
	}

	@PostConstruct
	private void afterInitialization() {
		getSyncDate();
		getFilterAttributes();
	}
	
	/** This method returns the sync date configured on the beio content root page's page properties. 
	 * @return sync date.
	 */
	public String getSyncDate() {
		LOG.debug("::::::: Entered getSyncDate method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(syncDate)) {
			return syncDate;
		}else {
			try(BEIOAutoClosingResourceResolver autoClosingResourceResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
				ResourceResolver resourceResolver = autoClosingResourceResolver.getResurceResolver();
				if(null!=resourceResolver) {
					Resource root = resourceResolver.resolve(BEIOConstants.BEIO_CONTENT_ROOT.concat(BEIOConstants.SLASH).concat(JcrConstants.JCR_CONTENT));
					if(CommonUtils.checkResource(root)) {
						ValueMap valueMap = root.getValueMap();
						if(null!=valueMap && valueMap.containsKey(BEIOConstants.SYNC_DATE)) {
							syncDate = valueMap.get(BEIOConstants.SYNC_DATE,String.class);
							LOG.debug("::::::: Exit from getSyncDate method of BEIOGenericListingUse Class :::::::");
							return syncDate;
						}
					}
				}
			} catch (IllegalArgumentException | LoginException e) {
				LOG.error(" :::::::  Exception in retrieving sync date in getSyncDate method of BEIOGenericListingUse Class ::::::: " , e);
			} catch (Exception e) {
				LOG.error(" ::::::: Broad Exception in retrieving sync date in getSyncDate method of BEIOGenericListingUse Class ::::::: " , e);
			}
			LOG.debug("::::::: Exit from getSyncDate method of BEIOGenericListingUse Class returned empty syncdate :::::::");
			return StringUtils.EMPTY;
		}
	}

	/**
	 * @param syncDate
	 */
	public void setSyncDate(String syncDate) {
		this.syncDate = syncDate;
	}
	
	/** This method creates the list of all the tags configured on the preview page and returns it.
	 * @return List of tags configured on the preview page.
	 */
	private List<String> getFilterAttributes() {
		LOG.debug("::::::: Entered getFilterAttributes method of BEIOGenericListingUse Class :::::::");
		try(BEIOAutoClosingResourceResolver autoClosingResourceResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			ResourceResolver resourceResolver = autoClosingResourceResolver.getResurceResolver();
			if(null!=resourceResolver) {
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				if(null!= tagManager) {
					if(null!=role) {
						role.forEach(r->{
							Tag tag = tagManager.resolve(r);
							if(null!=tag) {
								filterAttributes.add(tag.getName());
							}
						});
					}
					if(null!=therapeutic) {
						therapeutic.forEach(t->{
							Tag tag = tagManager.resolve(t);
							if(null!=tag) {
								filterAttributes.add(tag.getName());
							}
						});
					}
					if(null!=indication) {
						indication.forEach(i->{
							Tag tag = tagManager.resolve(i);
							if(null!=tag) {
								filterAttributes.add(tag.getName());
							}
						});
					}
					if(null!=products) {
						products.forEach(p->{
							Tag tag = tagManager.resolve(p);
							if(null!=tag) {
								filterAttributes.add(tag.getName());
							}
						});
					}
				}
			}
			LOG.debug("::::::: Exit from getFilterAttributes method of BEIOGenericListingUse Class :::::::");
		} catch (IllegalArgumentException | LoginException e) {
			LOG.error(" ::::::: Exception in constructing filter attributes in getFilterAttributes method of BEIOGenericListingUse Class :::::::" , e);
		} catch(Exception e) {
			LOG.error(" ::::::: Broader Exception in constructing filter attributes in getFilterAttributes method of BEIOGenericListingUse Class :::::::" , e);
		}
		return filterAttributes;
	}
	

	/** This method constructs a list of Product Promo Content Fragments and returns the list.
	 * @return List of Product Promo Content Fragments.
	 */
	public List<ProductPromoModel> getProductPromoList() {
		LOG.debug("::::::: Entered getProductPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(promosourcepath)) {
			String queryString = QueryBuilder.buildServletQuery(promosourcepath,BEIOConstants.PRODUCTS_PROMO_MODEL,syncDate,isPublish());
			ContentFragmentHelper<ProductPromoModel> productPromo = new ContentFragmentHelper<>();
			List<ProductPromoModel> productPromoList = productPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, ProductPromoModel.class,BEIOConstants.BEIO_ADMIN);
			queryString = null;
			LOG.debug("::::::: Exit from getProductPromoList method of BEIOGenericListingUse Class :::::::");
			return productPromoList;
		}
		LOG.debug("::::::: Exit from getProductPromoList method of BEIOGenericListingUse Class returned empty list as promosourcepath is blank :::::::");
		return new LinkedList<ProductPromoModel>();
	}
	
	/** This method constructs a list of Product Non-Promo Content Fragments and returns the list.
	 * @return List of Product Non Promo Content Fragments.
	 */
	public List<ProductNonPromoModel> getProductNonPromoList(){
		LOG.debug("::::::: Entered getProductNonPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(nonpromosourcepath)) {
			String queryString = QueryBuilder.buildServletQuery(nonpromosourcepath, BEIOConstants.PRODUCTS_NON_PROMO_MODEL, syncDate,isPublish());
			ContentFragmentHelper<ProductNonPromoModel> productNonPromo = new ContentFragmentHelper<>();
			List<ProductNonPromoModel> productNonPromoList = productNonPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, ProductNonPromoModel.class, BEIOConstants.BEIO_ADMIN);
			queryString = null;
			LOG.debug("::::::: Exit from getProductNonPromoList method of BEIOGenericListingUse Class :::::::");
			return productNonPromoList;
		}
		LOG.debug("::::::: Exit from getProductNonPromoList method of BEIOGenericListingUse Class returned empty list as nonpromosourcepath is blank :::::::");
		return new LinkedList<ProductNonPromoModel>();
	}
	
	/** This method constructs a list of BEIO Promo Content Fragments and returns the list.
	 * @return List of BEIO Promo Content Fragments.
	 */
	public List<BEIOPromoModel> getBEIOPromoList() {
		LOG.debug("::::::: Entered getBEIOPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(promosourcepath)) {
			ContentFragmentHelper<BEIOPromoModel> beioPromo = new ContentFragmentHelper<>();
			String queryString = QueryBuilder.buildServletQuery(promosourcepath, BEIOConstants.BEIO_PROMO_MODEL, syncDate,isPublish());
			List<BEIOPromoModel> beioPromoList = beioPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, BEIOPromoModel.class, BEIOConstants.BEIO_ADMIN);
			queryString = null;
			return beioPromoList;
		}
		LOG.debug("::::::: Exit from getBEIOPromoList method of BEIOGenericListingUse Class :::::::");
		return new LinkedList<BEIOPromoModel>();
	}

	/** This method constructs a list of BEIO Non-Promo Content Fragments and returns the list.
	 * @return List of BEIO Non-Promo Content Fragments.
	 */
	public List<BEIONonPromoModel> getBEIONonPromoList() {
		LOG.debug("::::::: Entered getBEIONonPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(nonpromosourcepath)) {
			ContentFragmentHelper<BEIONonPromoModel> beioPromo = new ContentFragmentHelper<>();
			String queryString = QueryBuilder.buildServletQuery(nonpromosourcepath, BEIOConstants.BEIO_NON_PROMO_MODEL, syncDate,isPublish());
			List<BEIONonPromoModel> beioNonPromoList = beioPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, BEIONonPromoModel.class, BEIOConstants.BEIO_ADMIN);
			queryString = null;
			return beioNonPromoList;
		}
		LOG.debug("::::::: Exit from getBEIONonPromoList method of BEIOGenericListingUse Class :::::::");
		return new LinkedList<BEIONonPromoModel>();
	}
	
	public List<MedinfoPromoModel> getMedinfoPromoList() {
		LOG.debug("::::::: Entered getMedinfoPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(promosourcepath)) {
			ContentFragmentHelper<MedinfoPromoModel> medinfoPromo = new ContentFragmentHelper<>();
			String queryString = QueryBuilder.buildServletQuery(promosourcepath, BEIOConstants.MEDINFO_PROMO_MODEL, syncDate,isPublish());
			List<MedinfoPromoModel> medinfoPromoList = medinfoPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, MedinfoPromoModel.class, BEIOConstants.BEIO_ADMIN);
			queryString = null;
			return medinfoPromoList;
		}
		LOG.debug("::::::: Exit from getMedinfoPromoList method of BEIOGenericListingUse Class :::::::");
		return new LinkedList<MedinfoPromoModel>();
	}
	public List<MedinfoNonPromoModel> getMedinfoNonPromoList() {
		LOG.debug("::::::: Entered getMedinfoNonPromoList method of BEIOGenericListingUse Class :::::::");
		if(StringUtils.isNotBlank(nonpromosourcepath)) {			
			ContentFragmentHelper<MedinfoNonPromoModel> medinfoNonPromo = new ContentFragmentHelper<>();
			String queryString = QueryBuilder.buildServletQuery(nonpromosourcepath, BEIOConstants.MEDINFO_NON_PROMO_MODEL, syncDate,isPublish());
			LOG.debug("::::::: queryString :::::::"+queryString);	
			List<MedinfoNonPromoModel> medinfoNonPromoList = medinfoNonPromo.getFragmentList(autoClosingResourceResolverFactory, queryString, MedinfoNonPromoModel.class, BEIOConstants.BEIO_ADMIN);
			queryString = null;
			return medinfoNonPromoList;
		}
		LOG.debug("::::::: Exit from getMedinfoNonPromoList method of BEIOGenericListingUse Class :::::::");
		return new LinkedList<MedinfoNonPromoModel>();
	}
	/** This method constructs a list of BEIO Promo Content Fragments, based on the filters configured in the
	 * Generic Listing component present on the preview page. This list will be used in the Generic Listing Component's HTML.
	 * @return List of BEIO Promo Content Fragments.
	 */
	public List<BEIOPromoModel> getHTLBEIOPromoList() {
		LOG.debug("::::::: Entered getHTLBEIOPromoList method of BEIOGenericListingUse Class :::::::");
		HTLBEIOPromoList = getBEIOPromoList();
		if(null!= HTLBEIOPromoList && HTLBEIOPromoList.size()>0) {
			if(null!= filterAttributes && filterAttributes.size()>0) {
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
									   .filter(BEIOUtils.getBEIOPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
										.and(BEIOUtils.getBEIOPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
										.and(BEIOUtils.getBEIOPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
										.and(BEIOUtils.getBEIOPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
										.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
										.sorted(CommonUtils::beioPromoSorter)
										.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIOPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIOPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIOPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIOPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS)))
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
				}

			}else {
				HTLBEIOPromoList = HTLBEIOPromoList.parallelStream().sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList());
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIOPromoList = HTLBEIOPromoList.parallelStream().sorted(CommonUtils::beioPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getShareable(), "true")) {
						HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLBEIOPromoList = HTLBEIOPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::beioPromoSorter)
								.collect(Collectors.toList());
					}
				}
			}
		}
		LOG.debug("::::::: Exit from getHTLBEIOPromoList method of BEIOGenericListingUse Class :::::::");
		return HTLBEIOPromoList;
	}


	/** This method constructs a list of BEIO Non Promo Content Fragments, based on the filters configured in the
	 * Generic Listing component present on the preview page. This list will be used in the Generic Listing Component's HTML.
	 * @return List of BEIO Non-Promo Content Fragments.
	 */
	public List<BEIONonPromoModel> getHTLBEIONonPromoList() {
		LOG.debug("::::::: Entered getHTLBEIONonPromoList method of BEIOGenericListingUse Class :::::::");
		HTLBEIONonPromoList = getBEIONonPromoList();
		if(null!= HTLBEIONonPromoList && HTLBEIONonPromoList.size()>0) {
			if(null!= filterAttributes && filterAttributes.size()>0) {
				HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream().filter(BEIOUtils.getBEIONonPromoPredicate(filterAttributes, autoClosingResourceResolverFactory)).sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList());
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIONonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIONonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIONonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
							   .filter(BEIOUtils.getBEIONonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getBEIONonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS)))
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
				}
			}else {
				HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream().sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList());
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream().sorted(CommonUtils::beioNonPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getDownloadable())) {
						HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getShareable())) {
						HTLBEIONonPromoList = HTLBEIONonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::beioNonPromoSorter)
								.collect(Collectors.toList());
					}
				}
			}
		}
		LOG.debug("::::::: Exit from getHTLBEIONonPromoList method of BEIOGenericListingUse Class :::::::");
		return HTLBEIONonPromoList;
	}

	/** This method constructs a list of Product Promo Content Fragments, based on the filters configured in the
	 * Generic Listing component present on the preview page. This list will be used in the Generic Listing Component's HTML.
	 * @return List of Product Promo Content Fragments.
	 */
	public List<ProductPromoModel> getHtlProductPromoList() {
		LOG.debug("::::::: Entered getHtlProductPromoList method of BEIOGenericListingUse Class :::::::");
		htlProductPromoList = getProductPromoList();
		if(null!= htlProductPromoList && htlProductPromoList.size()>0) {
			if(null!= filterAttributes && filterAttributes.size()>0) {
				if(StringUtils.equalsIgnoreCase("true", getDownloadable()) && StringUtils.equalsIgnoreCase("true", getShareable() )) {
					htlProductPromoList = htlProductPromoList.parallelStream()
							.filter(BEIOUtils.getProductsPromoGenericPredicate(getRoleTagNames(),BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsPromoGenericPredicate(getTherapeutic(),BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getIndication(),BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getProducts(),BEIOConstants.PRODUCTS))
									/*.and(BEIOUtils.getProductsPromoGenericPredicate(filterAttributes,StringUtils.EMPTY)*/
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
							.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
				}else if(StringUtils.equalsIgnoreCase("true", getDownloadable()) && StringUtils.equalsIgnoreCase("false", getShareable())) {
					htlProductPromoList = htlProductPromoList.parallelStream()
							.filter(BEIOUtils.getProductsPromoGenericPredicate(getRoleTagNames(),BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsPromoGenericPredicate(getTherapeutic(),BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getIndication(),BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getProducts(),BEIOConstants.PRODUCTS))
									/*.and(BEIOUtils.getProductsPromoGenericPredicate(filterAttributes,StringUtils.EMPTY)*/
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
							.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
				}else if(StringUtils.equalsIgnoreCase("true", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable())) {
					htlProductPromoList = htlProductPromoList.parallelStream()
							.filter(BEIOUtils.getProductsPromoGenericPredicate(getRoleTagNames(),BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsPromoGenericPredicate(getTherapeutic(),BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getIndication(),BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getProducts(),BEIOConstants.PRODUCTS))
									/*.and(BEIOUtils.getProductsPromoGenericPredicate(filterAttributes,StringUtils.EMPTY)*/
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
							.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
				}else if(null!= filterAttributes && filterAttributes.size()>0 && StringUtils.equalsIgnoreCase("false", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable())) {
					htlProductPromoList = htlProductPromoList.parallelStream()
							.filter(BEIOUtils.getProductsPromoGenericPredicate(getRoleTagNames(),BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsPromoGenericPredicate(getTherapeutic(),BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getIndication(),BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsPromoGenericPredicate(getProducts(),BEIOConstants.PRODUCTS)))
							.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
				}
			}else {
				if(StringUtils.equalsIgnoreCase("false", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable())) {
					htlProductPromoList = htlProductPromoList.parallelStream().sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase("true", getShareable()) && StringUtils.equalsIgnoreCase("true", getDownloadable())) {
						htlProductPromoList = htlProductPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getShareable())) {
						htlProductPromoList = htlProductPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getDownloadable())) {
						htlProductPromoList = htlProductPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::productPromoSorter).collect(Collectors.toList());
					}
					
				}
			}
		}
		LOG.debug("::::::: Exit from getHtlProductPromoList method of BEIOGenericListingUse Class :::::::");
		return htlProductPromoList;
	}
	
	/** This method constructs a list of Product Non-Promo Content Fragments, based on the filters configured in the
	 * Generic Listing component present on the preview page. This list will be used in the Generic Listing Component's HTML.
	 * @return List of Product Non-Promo Content Fragments.
	 */
	public List<ProductNonPromoModel> getHtlProductNonPromoList() {
		LOG.debug("::::::: Entered getHtlProductNonPromoList method of BEIOGenericListingUse Class :::::::");
		htlProductNonPromoList = getProductNonPromoList();
		//List<String> filterAttributes  = getFilterAttributes();
		if(null!= htlProductNonPromoList && htlProductNonPromoList.size()>0) {
			if(null!=filterAttributes && filterAttributes.size()>0) {
				if(StringUtils.equalsIgnoreCase("true", getDownloadable()) && StringUtils.equalsIgnoreCase("true", getShareable())) {
					htlProductNonPromoList = htlProductNonPromoList.parallelStream()
							.filter(BEIOUtils.getProductsNonPromoPredicate(filterAttributes, autoClosingResourceResolverFactory)
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
							.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
				}else if(StringUtils.equalsIgnoreCase("true", getDownloadable()) && StringUtils.equalsIgnoreCase("false", getShareable())) {
					htlProductNonPromoList = htlProductNonPromoList.parallelStream()
							.filter(BEIOUtils.getProductsNonPromoPredicate(filterAttributes, autoClosingResourceResolverFactory)
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
							.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
				}else if(StringUtils.equalsIgnoreCase("true", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable()) ) {
					htlProductNonPromoList = htlProductNonPromoList.parallelStream()
							.filter(BEIOUtils.getProductsNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
									.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
							.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
				}else if(StringUtils.equalsIgnoreCase("false", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable())) {
					htlProductNonPromoList = htlProductNonPromoList.parallelStream()
							.filter(BEIOUtils.getProductsNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
									.and(BEIOUtils.getProductsNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS)))
							.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
				}
			}else {
				if(StringUtils.equalsIgnoreCase("false", getShareable()) && StringUtils.equalsIgnoreCase("false", getDownloadable())) {
					htlProductNonPromoList = htlProductNonPromoList.parallelStream().sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase("true", getShareable()) && StringUtils.equalsIgnoreCase("true", getDownloadable())) {
						htlProductNonPromoList = htlProductNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getShareable())) {
						htlProductNonPromoList = htlProductNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase("true", getDownloadable())) {
						htlProductNonPromoList = htlProductNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::productNonPromoSorter).collect(Collectors.toList());
					}
					
				}
			}
		}
		LOG.debug("::::::: Exit from getHtlProductNonPromoList method of BEIOGenericListingUse Class :::::::");
		return htlProductNonPromoList;
	}
	public List<MedinfoPromoModel> getHTLMedinfoPromoList() {
		LOG.debug("::::::: Entered getHTLMedinfoPromoList method of BEIOGenericListingUse Class :::::::");

		HTLMedinfoPromoList = getMedinfoPromoList();
		if(null!= HTLMedinfoPromoList && HTLMedinfoPromoList.size()>0) {
			if(null!= filterAttributes && filterAttributes.size()>0) {
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
									   .filter(BEIOUtils.getMedinfoPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
										.and(BEIOUtils.getMedinfoPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
										.and(BEIOUtils.getMedinfoPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
										.and(BEIOUtils.getMedinfoPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
										.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
										.sorted(CommonUtils::medinfoPromoSorter)
										.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS)))
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
				}

			}else {
				HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream().sorted(CommonUtils::medinfoPromoSorter).collect(Collectors.toList());
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream().sorted(CommonUtils::medinfoPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getShareable(), "true")) {
						HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLMedinfoPromoList = HTLMedinfoPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::medinfoPromoSorter)
								.collect(Collectors.toList());
					}
				}
			}
		}
		LOG.debug("::::::: Exit from getHTLMedinfoPromoList method of BEIOGenericListingUse Class :::::::");
		return HTLMedinfoPromoList;
	}
	public List<MedinfoNonPromoModel> getHTLMedinfoNonPromoList() {
		LOG.debug("::::::: Entered getHTLMedinfoNonPromoList method of BEIOGenericListingUse Class :::::::");

		HTLMedinfoNonPromoList = getMedinfoNonPromoList();
		if(null!= HTLMedinfoNonPromoList && HTLMedinfoNonPromoList.size()>0) {
			if(null!= filterAttributes && filterAttributes.size()>0) {
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
									   .filter(BEIOUtils.getMedinfoNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
										.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
										.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
										.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
										.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable()) && StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
										.sorted(CommonUtils::medinfoNonPromoSorter)
										.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), getShareable());}))
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
					HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS))
								.and(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), getDownloadable());}))
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
				}
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
							   .filter(BEIOUtils.getMedinfoNonPromoGenericPredicate(getRoleTagNames(), BEIOConstants.ROLE)
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getTherapeutic(), BEIOConstants.THERUPATIC_AREA))	   
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getIndication(), BEIOConstants.INDICATION))
								.and(BEIOUtils.getMedinfoNonPromoGenericPredicate(getProducts(), BEIOConstants.PRODUCTS)))
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
				}

			}else {
				HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream().sorted(CommonUtils::medinfoNonPromoSorter).collect(Collectors.toList());
				if(StringUtils.equalsIgnoreCase(getShareable(), "false") && StringUtils.equalsIgnoreCase(getDownloadable(), "false")) {
					HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream().sorted(CommonUtils::medinfoNonPromoSorter).collect(Collectors.toList());
				}else {
					if(StringUtils.equalsIgnoreCase(getShareable(), "true") && StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true") && StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getShareable(), "true")) {
						HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getShareable(), "true");})
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
					}else if(StringUtils.equalsIgnoreCase(getDownloadable(), "true")) {
						HTLMedinfoNonPromoList = HTLMedinfoNonPromoList.parallelStream()
								.filter(p->{ return StringUtils.equalsIgnoreCase(p.getDownloadable(), "true");})
								.sorted(CommonUtils::medinfoNonPromoSorter)
								.collect(Collectors.toList());
					}
				}
			}
		}
		LOG.debug("::::::: Exit from getHTLMedinfoNonPromoList method of BEIOGenericListingUse Class :::::::");
		return HTLMedinfoNonPromoList;
	}
}
