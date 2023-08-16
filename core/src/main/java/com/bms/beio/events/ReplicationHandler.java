package com.bms.beio.events;

import java.util.Calendar;
import java.util.Optional;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.config.BmsBeioConfig;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.utils.CommonUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;

/**
 * @author marri.shashanka
 *
 */
@Component(service=EventHandler.class, property= {"event.topics="+ReplicationAction.EVENT_TOPIC},immediate=true)
public class ReplicationHandler implements EventHandler{
	
	Logger LOG = LoggerFactory.getLogger(ReplicationHandler.class);
	
	@Reference
	private ResourceResolverFactory resoureResolverFactory;
	
	@Reference
	private SlingSettingsService slingSettingService;
	
	@Reference
	private BmsBeioConfig bmsBeioConfig;
	
	@Reference
	private Replicator replicator;
	
	@Reference 
	private AgentManager agentManager;
	
	@Reference
	private BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	private String damRoot;
	
	private String[] replicationAgents;
	
	@Activate
	protected void activate(ComponentContext context) {
		LOG.debug(":::::::::: Entered Activate Method of ReplicationHandler ::::::::::");
		damRoot = bmsBeioConfig.getDamRoot();
		if(StringUtils.isBlank(damRoot)) {
			damRoot = BEIOConstants.ASSET_ROOT;
		}
		replicationAgents = bmsBeioConfig.getReplicationAgents();
		LOG.debug(":::::::::: Exit from Method of ReplicationHandler ::::::::::");
	}

	@Override
	public void handleEvent(Event event) {
        LOG.debug(":::::::::: Entered handleEvent Method of ReplicationHandler ::::::::::");
        if(slingSettingService.getRunModes().contains("author")) {
              if(null!=event) {
            	  try(BEIOAutoClosingResourceResolver autoClosingResourceResourceResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
            		  ResourceResolver resourceResolver = autoClosingResourceResourceResolver.getResurceResolver();
            		  ReplicationAction action = ReplicationAction.fromEvent(event);
                      if(null!=action && null!=resourceResolver) {
                            if((action.getType()==ReplicationActionType.ACTIVATE || action.getType()==ReplicationActionType.DEACTIVATE) && StringUtils.isNotBlank(action.getPath()) && StringUtils.startsWith(action.getPath(), damRoot)) {
                          	  LOG.info(" ::::::: Replication Event being triggered for ::::::: " + action.getPath());
                                  Resource replicatedResource = resourceResolver.resolve(action.getPath());
                                  if( StringUtils.equalsIgnoreCase(CommonUtils.getProperty(replicatedResource, JcrConstants.JCR_PRIMARYTYPE), DamConstants.NT_DAM_ASSET)) {
                                        Resource jcrContent = replicatedResource.getChild(JcrConstants.JCR_CONTENT);
                                        if(CommonUtils.checkResource(jcrContent)) {
                                              Optional<Resource> jcrContentNullChk = Optional.of(jcrContent);
                                              Optional<Boolean>hasContentFragment = jcrContentNullChk.map(Resource::getValueMap).map(vm->vm.get(BEIOConstants.CONTENT_FRAGMENT,Boolean.class));
                                              if(hasContentFragment.isPresent() && hasContentFragment.get()){
                                                    Optional<Resource> data = jcrContentNullChk.map(rs->rs.getChild(BEIOConstants.DATA));
                                                    if(CommonUtils.checkResource(data.get())) {
                                                          Optional<String> contentFragmentModel = data.map(Resource::getValueMap).map(vm->vm.get(BEIOConstants.CQ_MODEL,String.class));
                                                          if(contentFragmentModel.isPresent() && StringUtils.isNotBlank(contentFragmentModel.get()) && 
                                                                               (StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.NEWS_MODEL) || 
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.PRODUCTS_PROMO_MODEL) ||
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.PRODUCTS_NON_PROMO_MODEL)  || 
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.BEIO_PROMO_MODEL) || 
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.BEIO_NON_PROMO_MODEL) || 
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.MEDINFO_PROMO_MODEL) || 
                                                                                  StringUtils.equalsIgnoreCase(contentFragmentModel.get(), BEIOConstants.MEDINFO_NON_PROMO_MODEL))){
                                                                                                                                                        
                                                                Optional<Resource> master = data.map(rs->rs.getChild(BEIOConstants.MASTER));
                                                                if(CommonUtils.checkResource(master.get())){
                                                                      ModifiableValueMap modifiableValueMap = master.get().adaptTo(ModifiableValueMap.class);
                                                                      if(action.getType()==ReplicationActionType.ACTIVATE) {
                                                                      	LOG.info("::::::: Publish Date added to Node :::::: " + action.getPath() + " ::::::: " + Calendar.getInstance());
                                                                            modifiableValueMap.put(BEIOConstants.PUBLISH_DATE, Calendar.getInstance());  
                                                                      }else if(action.getType()==ReplicationActionType.DEACTIVATE && modifiableValueMap.containsKey(BEIOConstants.PUBLISH_DATE)) {
                                                                            modifiableValueMap.remove(BEIOConstants.PUBLISH_DATE);
                                                                      }
                                                                      Session currentSession = resourceResolver.adaptTo(Session.class);
                                                                      try {
                                                                            if(null!=currentSession) {
                                                                                  currentSession.save();
                                                                            }
                                                                      } catch (RepositoryException e) {
                                                                            LOG.error("::::::: Exception in adding publish date to the content fragment in handleEvent Method of ReplicationHandler class:::::::" , e);
                                                                      }
                                                                      if(action.getType()==ReplicationActionType.ACTIVATE) {
                                                                      	CommonUtils.replicateNode(master.get().getPath(),autoClosingResourceResolverFactory,replicator, agentManager, replicationAgents);
                                                                      } // code has to be moved to shared project.
                                                                }
                                                          }
                                                    }
                                              }                                   
                                        }
                                  }
                            }
                      }
            	  } catch (IllegalArgumentException | LoginException e) {
            		  LOG.error(" ::::::: Exception in processing replication event in handleEvent Method of ReplicationHandler class:::::::" , e); 
            	  } catch(Exception e) {
            		  LOG.error(" ::::::: Broad Exception in processing replication event in handleEvent Method of ReplicationHandler class:::::::" , e);
            	  }
              }
        }
        LOG.debug(":::::::::: Exit from handleEvent Method of ReplicationHandler ::::::::::");
   }
}
