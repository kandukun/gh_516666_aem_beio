package com.bms.beio.events;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.config.BmsBeioConfig;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.utils.CommonUtils;
import com.bms.beio.utils.IdGenerator;
import com.day.cq.dam.api.DamConstants;
/**
 * @author marri.shashanka
 *
 */
@Component(service = BEIOAssetHandler.class, immediate = true)
public class BEIOAssetHandler implements EventListener {

	Logger LOG = LoggerFactory.getLogger(BEIOAssetHandler.class);
	
	@Reference
	private ResourceResolverFactory resoureResolverFactory;
	@Reference
	private SlingSettingsService slingSettingService;
	@Reference
	private BmsBeioConfig beioconfig;
	@Reference
	private BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	ObservationManager observationManager;
	Session session;
	ResourceResolver resourceResolver;

	@Activate
	protected void activate(ComponentContext context) {
		LOG.debug(":::::::::: Entered activate method of AssetHandler ::::::::::");
		try {
			if(slingSettingService.getRunModes().contains("author")) {
				Map<String, Object> authMap = new HashMap<String, Object>();
				authMap.put(ResourceResolverFactory.SUBSERVICE, BEIOConstants.BEIO_ADMIN);
				 resourceResolver = resoureResolverFactory.getServiceResourceResolver(authMap);

				if(null!=resourceResolver) {
					session = resourceResolver.adaptTo(Session.class);
					if(null!=session) {
						observationManager = session.getWorkspace().getObservationManager();
						final String[] nodeTypes = { DamConstants.NT_DAM_ASSET, JcrConstants.NT_UNSTRUCTURED };
						String path = beioconfig.getDamRoot();
						if(StringUtils.isBlank(path)) {
							path = BEIOConstants.ASSET_ROOT;
						}
						//final String path = BMSConstants.ASSET_ROOT;
						observationManager.addEventListener(this, Event.NODE_ADDED, path, true, null, nodeTypes, false);
					}
				}
			}
			
		} catch (LoginException | RepositoryException e) {
			LOG.error("Exception in getting Resource Resolver in Activate Method of AssetHandler class" , e);
		}
		LOG.debug(":::::::::: Event Listener Bundle Activated ::::::::::");
		LOG.debug(":::::::::: Exit from activate method of AssetHandler ::::::::::");
	}

	@Override
	public void onEvent(EventIterator events) {
		if(slingSettingService.getRunModes().contains("author")) {
			LOG.debug(":::::::::: Entered onEvent method of AssetHandler ::::::::::");
			while (events.hasNext()) {
				Event event = events.nextEvent(); 
				try {
					if (StringUtils.endsWith(event.getPath(), JcrConstants.JCR_CONTENT)){
						addUniqueId(event.getPath());
					}
				} catch (RepositoryException e) {
					LOG.error("::::: Exception in getting Event in Asset Handler class :::::" , e);
				}
			}
			LOG.debug(":::::::::: Exit from onEvent method of AssetHandler ::::::::::");
		}
	}
	
	/**
	 * @param path
	 * 
	 * This method takes the path of the newly added content fragment and 
	 * adds a property called content_fragment_unique_id to that fragment.
	 *  
	 */
	private void addUniqueId(String path) {
		LOG.debug(":::::::::: Entered addUniqueId method of AssetHandler ::::::::::");
		if(StringUtils.isNotEmpty(path)) {
			try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)) {
				ResourceResolver resolver = autoClosingResolver.getResurceResolver();
				if(null!=resolver) {
					Resource jcr_content = resolver.resolve(path);
					Optional<Resource> jcr_contentNullchk = Optional.ofNullable(jcr_content);
					Optional<Boolean> fragmentChk = jcr_contentNullchk.map(Resource::getValueMap).map(vp->vp.get(BEIOConstants.CONTENT_FRAGMENT,Boolean.class)); 
					if(!ResourceUtil.isNonExistingResource(jcr_content)) {
						if(fragmentChk.isPresent() && fragmentChk.get()) {
							Resource parent = jcr_content.getParent();
							String timeStamp = StringUtils.EMPTY;
							if(CommonUtils.checkResource(parent)) {
								ValueMap parentValueMap = parent.getValueMap();
								if(null!=parentValueMap && parentValueMap.containsKey(JcrConstants.JCR_CREATED)) {
									timeStamp = parentValueMap.get(JcrConstants.JCR_CREATED, String.class);
								}
							}
							Resource data = jcr_content.getChild(BEIOConstants.DATA);
							if(CommonUtils.checkResource(data)) {
								Resource master = data.getChild(BEIOConstants.MASTER);
								if(CommonUtils.checkResource(master)) {
									ModifiableValueMap valueMap =  master.adaptTo(ModifiableValueMap.class); 
									String uniqueId = IdGenerator.generateUniqueID(timeStamp);
									if(StringUtils.isNotBlank(uniqueId) && null!=valueMap) {
										valueMap.put(BEIOConstants.CF_UNIQUE_ID,uniqueId);
										valueMap.put(BEIOConstants.CREATION_DATE, Calendar.getInstance());
										try {
											Session session = resolver.adaptTo(Session.class);
											if(null!=session) {
												session.save();
											}
										} catch (RepositoryException e) {
											LOG.error(":::::::::: Unable to save CF_UNIQUE_ID ::::::::::" , e);
										}
									}
								}
							}
						}
					}
				}
			} catch(IllegalArgumentException | LoginException e) {
				LOG.error(":::::::::: Exception getting Resource Resolver in  addUniqueId method of AssetHandler Class ::::::::::" , e);
			} catch (Exception e) {
				LOG.error(":::::::::: Broad Exception in addUniqueId method of AssetHandler Class ::::::::::" , e);
			}
		}
		LOG.debug(":::::::::: Exit from addUniqueId method of AssetHandler ::::::::::");
	}
}
