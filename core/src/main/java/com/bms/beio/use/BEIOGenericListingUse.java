package com.bms.beio.use;

import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.GenericListingComponentModel;

/**
 * @author sandeep.kumar.kusam
 *
 */
public class BEIOGenericListingUse extends WCMUsePojo{

Logger LOG = LoggerFactory.getLogger(BEIOGenericListingUse.class);
	
	@Reference
	private BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	GenericListingComponentModel genericListingComponentModel;
	
    /**
     * 
     * @Overridden
     * @see com.adobe.cq.sightly.WCMUsePojo#activate()
     * This method constructs the genericListingComponentModel for the BEIOGenericListingUse class
     * 
     */
	
	@Override
	public void activate() throws Exception {
		autoClosingResourceResolverFactory = getSlingScriptHelper().getService(BEIOAutoClosingResourceResolverFactory.class);
		if(null!=autoClosingResourceResolverFactory) {
			try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
				ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
				String path = getCurrentPage().getPath().concat(BEIOConstants.SLASH).concat("jcr:content").concat(BEIOConstants.SLASH).concat(BEIOConstants.ROOT);
				Resource root = resourceResolver.resolve(path);
				Iterable<Resource> children = root.getChildren(); 
				for(Resource resource : children){
					if(StringUtils.equalsIgnoreCase(resource.getResourceType(), BEIOConstants.BEIO_GENERIC_LISTING_TEMPLATE)) {
						Optional<Resource> resourceNullchk = Optional.of(resource);
						if(resourceNullchk.isPresent()) {
							genericListingComponentModel = resource.adaptTo(GenericListingComponentModel.class);
						}
					}
				}
			}catch (LoginException | IllegalArgumentException e) {
				LOG.error(" ::::::: Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
			}catch (Exception e) {
				LOG.error(" ::::::: Broad Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
			}
		}
	}

	/**
     * This method gets the genericListingComponentModel
     */
	public GenericListingComponentModel getGenericListingComponentModel() {
		return genericListingComponentModel;
	}

	/**
     * This method sets the genericListingComponentModel
     */
	public void setGenericListingComponentModel(GenericListingComponentModel genericListingComponentModel) {
		this.genericListingComponentModel = genericListingComponentModel;
	}
	
}
