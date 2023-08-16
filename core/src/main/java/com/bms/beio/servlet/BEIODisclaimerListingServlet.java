package com.bms.beio.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.DisclaimerModel;
import com.bms.beio.utils.QueryBuilder;
import com.bms.bmscorp.core.utils.BMSResourceResolverUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bms.beio.utils.ContentFragmentHelper;

@Component(service=Servlet.class,property= {
		ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES+"=beio-mobile-app/components/page/beio-locale-page",
		ServletResolverConstants.SLING_SERVLET_METHODS+"=GET",
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS+"=mobile",
		ServletResolverConstants.SLING_SERVLET_SELECTORS+"=json"
},immediate=true)
public class BEIODisclaimerListingServlet extends SlingSafeMethodsServlet{

	private static final long serialVersionUID = 1L;
	
	private transient Logger LOG = LoggerFactory.getLogger(BEIODisclaimerListingServlet.class);
	
	@Reference
	private transient BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory;
	
	@Reference
	private transient SlingSettingsService slingService;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
			ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
			String currentPagePath = request.getRequestPathInfo().getResourcePath();
			Resource resource = resourceResolver.getResource(currentPagePath);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonResponse =  mapper.createObjectNode();
			if(resource!=null){
				ValueMap properties = resource.getValueMap();
				String contentFragmentsPath = properties.get("disclaimerPath",String.class);
				if(StringUtils.isNotEmpty(contentFragmentsPath)){
					String queryString = QueryBuilder.buildServletQuery(contentFragmentsPath,
							BEIOConstants.BEIO_DISCLAIMER_MODEL,StringUtils.EMPTY,BMSResourceResolverUtil.isPublish(slingService));
					ContentFragmentHelper<DisclaimerModel> fragments = new ContentFragmentHelper<DisclaimerModel>();
					List<DisclaimerModel> fragmentsList = fragments.getFragmentList(autoClosingResourceResolverFactory, queryString,
															DisclaimerModel.class, BEIOConstants.BEIO_ADMIN);
					jsonResponse.set("Disclaimers", mapper.valueToTree(fragmentsList));
					
				}
			}
			response.setContentType("text/json");
			response.getWriter().write(mapper.writeValueAsString(jsonResponse));
		}catch (LoginException | IllegalArgumentException e) {
			LOG.error(" ::::::: Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
		} catch (Exception e) {
			LOG.error(" ::::::: Broad Exception in doGet Method of BEIOGenericListingServlet ::::::: " , e);
		}
		
	}

}
