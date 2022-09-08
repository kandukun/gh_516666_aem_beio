package com.bms.beio.utils;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.events.ResourceReplicator;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.BaseModel;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.MedinfoNonPromoModel;
import com.bms.beio.slingmodels.MedinfoPromoModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentIdFilter;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
//import com.bms.bmscorp.core.service.BMSDomainFactory;

/**
 * @author marri.shashanka
 *
 *This is a class containing static utility methods with common functionality 
 *used across multiple locations in the project
 *
 */
public class CommonUtils {
	static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);
	
	private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.*?)</a>";
	private static final String HTML_A_TITLE_TAG_PATTERN = "\\s*(?i)title\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_A_ID_TAG_PATTERN = "\\s*(?i)id\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_A_CLASS_TAG_PATTERN = "\\s*(?i)class\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_A_TARGET_TAG_PATTERN = "\\s*(?i)target\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_A_DOWNLOAD_TAG_PATTERN = "\\s*(?i)download\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String HTML_IMG_TAG_PATTERN = "<img([^>]*[^/])>";
	private static final String HTML_IMG_SRC_PATTERN = "\\s*(?i)src\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";

	/**
	 * This is a utility method that checks whether the passed in resource is
	 * not and is an existing resource in the repository.
	 * 
	 * @param resource
	 * @return true if resource exists
	 * @return false if resource doesn't exist
	 * 
	 */
	public static boolean checkResource(Resource resource) {
		LOG.debug("::::::: Entered checkResource method of CommonUtils class :::::::");
		if(null!=resource && !ResourceUtil.isNonExistingResource(resource)) {
			LOG.debug("::::::: Exit from checkResource method of CommonUtils class *** Returned true :::::::");
			return true;
		}
		LOG.debug("::::::: Exit from checkResource method of CommonUtils class *** Returned false :::::::");
		return false;
	}

	/**
	 * @param bytes
	 * @return User friendly size.
	 * 
	 * This is a utility method that takes the size of a file in bytes 
	 * and converts into a appropriate user friendly and readable format.
	 */
	public static String calculateProperFileSize(double bytes){
		LOG.debug("::::::: Entered calculateProperFileSize method of CommonUtils class :::::::");
		String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
		String sizeToReturn = StringUtils.EMPTY;
		int index = 0;
		for(index = 0; index < fileSizeUnits.length; index++){
			if(bytes < 1024){
				break;
			}
			bytes = bytes / 1024;
		}
		DecimalFormat df = new DecimalFormat("#.##"); 
		sizeToReturn = String.valueOf(df.format(bytes)) + fileSizeUnits[index];
		LOG.debug("::::::: Exit from calculateProperFileSize method of CommonUtils class :::::::");
		return sizeToReturn;
	}

	/**
	 * @param resource
	 * @param property
	 * @return specified property from the resource
	 * 
	 * This is a static utility method that extracts the specified property from the
	 * given resource and returns it.
	 */
	public static String getProperty(Resource resource, String property) {
		LOG.debug("::::::: Entered getProperty method of CommonUtils class :::::::");
		String value = StringUtils.EMPTY;
		if(checkResource(resource)) {
			if(StringUtils.isNotBlank(property)) {
				ValueMap valueMap = resource.getValueMap();
				if(null!=valueMap && valueMap.containsKey(property)) {
					LOG.debug("::::::: Exit from getProperty method of CommonUtils class *** returning value from ValueMap:::::::");
					return valueMap.get(property,String.class);
				}
			}
		}
		LOG.debug("::::::: Exit from getProperty method of CommonUtils class *** returning empty value :::::::");
		return value;
	}

	
	/**
	 * This is a static utility method to remove a particular a specific element from the 
	 * provided string.
	 * @param content
	 * @param tag
	 * @return returns a string without the specified element.
	 */
	public static String removeTag(String content,String tag){
		LOG.debug("::::::: Entered removeTag method of CommonUtils class :::::::");
		content = StringUtils.remove(content, tag);
		LOG.debug("::::::: Exit from removeTag method of CommonUtils class :::::::");
		return content;
	}

	/**
	 * @param text
	 * @param className
	 * @param domainFactory
	 * @param isPublish
	 * @param request
	 * @return anchor tag's text value
	 * @throws URISyntaxException
	 */
	public static String getHrefTextValue(String text, String className, BMSDomainFactory domainFactory, boolean isPublish, SlingHttpServletRequest request) throws URISyntaxException {
		LOG.debug("::::::: Entered getHrefTextValue method of CommonUtils class :::::::");
		if (StringUtils.isBlank(text)) {
			text = StringUtils.EMPTY;
		}
		if (StringUtils.isBlank(className)) {
			className = StringUtils.EMPTY;
		}
		String resultText = null;
		if (!StringUtils.isBlank(text)) {
			Pattern p = Pattern.compile(HTML_A_TAG_PATTERN);
			Matcher matcher = p.matcher(text);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// any complex logic can be placed here
				String url = matcher.group(0);
				String href = null;
				Pattern hrefCompliler = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
				Matcher hrefMatcher = hrefCompliler.matcher(url);
				if (hrefMatcher.find()) {
					href = hrefMatcher.group(1);
					href = href.replace("\"", "");
				}
				String id = "";
				Pattern idCompliler = Pattern.compile(HTML_A_ID_TAG_PATTERN);
				Matcher idMatcher = idCompliler.matcher(url);
				if (idMatcher.find()) {

					id = idMatcher.group(1);
					id = id.replace("\"", "");
				}
				String urlText = StringUtils.EMPTY;
				Pattern titleFinder = Pattern.compile("<a[^>]*>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
				Matcher regexMatcher = titleFinder.matcher(url);
				while (regexMatcher.find()) {
					urlText = regexMatcher.group(1);
				}
				LOG.debug("urlText is::::::{}", urlText);
				String title = StringUtils.EMPTY;
				Pattern titleCompliler = Pattern.compile(HTML_A_TITLE_TAG_PATTERN);
				Matcher titleMatcher = titleCompliler.matcher(url);
				if (titleMatcher.find()) {
					title = titleMatcher.group(1);
					title = title.replace("\"", "");
				}
				String target = StringUtils.EMPTY;
				Pattern targetCompliler = Pattern.compile(HTML_A_TARGET_TAG_PATTERN);
				Matcher targetMatcher = targetCompliler.matcher(url);
				if (targetMatcher.find()) {
					target = targetMatcher.group(1);
					LOG.debug("target value found is " + target);
					target = target.replace("\"", "");
					LOG.debug("target value replace is " + target);
				}
				String download = StringUtils.EMPTY;
				Pattern downloadCompliler = Pattern.compile(HTML_A_DOWNLOAD_TAG_PATTERN);
				Matcher downloadMatcher = downloadCompliler.matcher(url);
				if (downloadMatcher.find()) {
					download = downloadMatcher.group(1);
					LOG.debug("download value found is " + download);
					download = download.replace("\"", "");
					LOG.info("download value replace is " + download);
					download = "download = \'" + download + "'";
				}

				else {
					download = "";
					LOG.debug("downloads value if blank" + download);
				}

				Pattern classCompliler = Pattern.compile(HTML_A_CLASS_TAG_PATTERN);
				Matcher classMatcher = classCompliler.matcher(url);
				if (classMatcher.find()) {
					className = classMatcher.group(1);
					className = className.replace("\"", "");
				}
				if (href != null && href.startsWith("#")) {
					resultText = ("<a href='" + href + "' title='" + title + "' class='" + className + "' target='"
							+ target + "' aria-label='" + title + "'>" + urlText + "</a>");
				} 
				else if (StringUtils.isBlank(href) && !StringUtils.isBlank(id)) {
					resultText = ("<a id='" + id + "'></a>");
				}else {
					resultText = getHrefValue(href, urlText, target, className, title, download, resultText, request, domainFactory, isPublish);
				}
				matcher.appendReplacement(sb, resultText);
				if (StringUtils.isBlank(className)) {
					className = StringUtils.EMPTY;
				}
			}
			matcher.appendTail(sb);
			String result = sb.toString();
			resultText = result;
		}
		LOG.debug("::::::: Exit from getHrefTextValue method of CommonUtils class :::::::");
		return resultText;
	}
	
	/**
	 * 
	 * @param linkPath
	 * @param urlText
	 * @param target
	 * @param className
	 * @param arialabel
	 * @param download
	 * @param resultText
	 * @param request
	 * @param domainFactory
	 * @param isPublish
	 * @return anchor tag's href value
	 * @throws URISyntaxException
	 */
	public static String getHrefValue(String linkPath, String urlText, String target, String className, String arialabel,
			String download, String resultText, SlingHttpServletRequest request,BMSDomainFactory domainFactory, boolean isPublish) throws URISyntaxException {
		LOG.debug("::::::: Entered getHrefValue method of CommonUtils class :::::::");
		String transformedPath = null;
		if(linkPath==null){
			return linkPath;
		}
		StringBuffer sb = new StringBuffer();
		if (linkPath.length() > 1 && linkPath.endsWith("/")) {
			linkPath = linkPath.substring(0, linkPath.length() - 1);
		}
		if (linkPath.startsWith("#") || linkPath.equals("/") || linkPath.startsWith("mailto:")
				|| linkPath.startsWith("tel:")) {

			resultText = "<a href='" + linkPath + "' title='" + arialabel + "' class='" + className
					+ "' target='" + target + "' aria-label='" + arialabel + "' " + download + "  >" + urlText
					+ "</a>";
			return resultText;

		}
		LOG.debug("gethrefvalue resulttext " + resultText);
		if(linkPath.startsWith(BEIOConstants.HTTP_SLASH) ||linkPath.startsWith(BEIOConstants.HTTPS_SLASH)) {
			//External Links
			sb.append("<a href='" + linkPath + "' title='" + arialabel + "' class='" + className
					+ "' target='" + target + "' aria-label='" + arialabel + "'>" + urlText + "</a>");
			return sb.toString();
		}		
		else{
			//Internal Link Or asset Path
			transformedPath = linkPath;
			
			if(linkPath.startsWith((BEIOConstants.CONTENT_ROOT))) {
				transformedPath = domainFactory.getEndUserExternalLink(linkPath, isPublish, request);	
			}
			sb.append("<a href='" + transformedPath + "' title='" + arialabel + "' class='" + className
					+ "' target='" + target + "' aria-label='" + arialabel + "'>" + urlText + "</a>");
		}
		String result = sb.toString();
		resultText = result;
		LOG.debug("::::::: Exit from getHrefValue method of CommonUtils class :::::::");
		return resultText;
	}
	
	
	/**
	 * @param text
	 * @param className
	 * @param domainFactory
	 * @param isPublish
	 * @param request
	 * @return String containing externalized href attribute of anchor <a> tag
	 * @throws URISyntaxException
	 * 
	 * This is a static utility method which searches for <a> anchor tag in the
	 * input string passed and externalizes the href attribute of the image tag
	 * and also shortens it. The url will be different for different environments.
	 * 
	 */
	public static String getImgTextValue(String text, String className, BMSDomainFactory domainFactory, boolean isPublish, SlingHttpServletRequest request) throws URISyntaxException {
		LOG.debug("::::::: Entered getHrefTextValue method of CommonUtils class :::::::");
		if (StringUtils.isBlank(text)) {
			text = StringUtils.EMPTY;
		}
		if (StringUtils.isBlank(className)) {
			className = StringUtils.EMPTY;
		}
		String resultText = null;
		if (!StringUtils.isBlank(text)) {
			Pattern p = Pattern.compile(HTML_IMG_TAG_PATTERN);
			Matcher matcher = p.matcher(text);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// any complex logic can be placed here
				String url = matcher.group(0);
				String src = null;
				Pattern srcCompliler = Pattern.compile(HTML_IMG_SRC_PATTERN);
				Matcher srcMatcher = srcCompliler.matcher(url);
				if (srcMatcher.find()) {
					src = srcMatcher.group(1);
					src = src.replace("\"", "");
				}
				resultText = getImgValue(src, request, domainFactory, isPublish);
				matcher.appendReplacement(sb, resultText);
				if (StringUtils.isBlank(className)) {
					className = StringUtils.EMPTY;
				}
			}
			matcher.appendTail(sb);
			String result = sb.toString();
			resultText = result;
		}
		LOG.debug("::::::: Exit from getHrefTextValue method of CommonUtils class :::::::");
		return resultText;
	}
	
	/**
	 * @param linkPath
	 * @param request
	 * @param domainFactory
	 * @param isPublish
	 * @return String containing externalized src attribute of image <img> tag
	 * @throws URISyntaxException
	 * 
	 * This is a static utility method which searches for <img> image tag in the
	 * input string passed and externalizes the source attribute of the image tag
	 * and also shortens it. The url will be different for different environments.
	 * 
	 */
	public static String getImgValue(String linkPath, SlingHttpServletRequest request,BMSDomainFactory domainFactory, boolean isPublish) throws URISyntaxException {
		LOG.debug("::::::: Entered getImgValue method of CommonUtils class :::::::");
		String transformedPath = null;
		if(linkPath==null){
			return linkPath;
		}
		StringBuffer sb = new StringBuffer();
		if (linkPath.length() > 1 && linkPath.endsWith("/")) {
			linkPath = linkPath.substring(0, linkPath.length() - 1);
		}
		if(linkPath.startsWith(BEIOConstants.HTTP_SLASH) ||linkPath.startsWith(BEIOConstants.HTTPS_SLASH)) {
			//External Links
			sb.append("<img src='" + linkPath + "'>" + "</img>");
			return sb.toString();
		}		
		else{
			//Internal Link Or asset Path
			transformedPath = linkPath;
			if(linkPath.startsWith((BEIOConstants.CONTENT_ROOT))) {
				transformedPath = domainFactory.getEndUserExternalLink(linkPath, isPublish, request);	
			}
			sb.append("<img src='" + transformedPath + "'>" + "</img>");
		}
		LOG.debug("::::::: Exit from getImgValue method of CommonUtils class :::::::");
		return sb.toString();
	}
	
	/**
	 * This is a static utility method that takes a list of tags
	 * and return a list of corresponding titles of the tags.
	 * @param tags
	 * @param autoClosingResourceresolver
	 * @return List of Tags
	 */
	public static List<String> getTagList(List<String> tags,BEIOAutoClosingResourceResolverFactory autoClosingResourceresolver,String serviceUser) {
		LOG.debug("::::::: Entered from getTagList method of CommonUtils class :::::::");
		List<String> tagArray = new ArrayList<>();
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceresolver.getResourceResolver(serviceUser)) {
			ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
			Optional<List<String>> tagsNulllCheck = Optional.ofNullable(tags);
			if(tagsNulllCheck.isPresent() && tagsNulllCheck.get().size()>0) {
				if(null!=resourceResolver) {
					TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
					if(null!=tagManager) {
						for(String tag:tagsNulllCheck.get()) {
							Tag tz = tagManager.resolve(tag);
							if(null!=tz) {
								tagArray.add(tz.getTitle());
							}
						}
					}
				}
			}
		} catch (IllegalArgumentException | LoginException e) {
			LOG.error("::::::: Exception in resolving tags in getTagList method of CommonUtils class ::::::: " , e);
		} catch (Exception e1) {
			LOG.error("::::::: Broad Exception in resolving tags in getTagList method of CommonUtils class ::::::: " , e1);
		} 
		LOG.debug("::::::: Exit from getTagList method of CommonUtils class :::::::");
		return tagArray;
	}
	
	/**
	 * @param tags
	 * @param autoClosingResourceresolver
	 * @return List of Tags
	 * 
	 * This is a static utility method that takes a list of tags
	 * and return a list of corresponding titles of the tags.
	 * 
	 */
	public static List<String> getTagNameList(List<String> tags,BEIOAutoClosingResourceResolverFactory autoClosingResourceresolver,String serviceUser) {
		LOG.debug("::::::: Entered from getTagList method of CommonUtils class :::::::");
		List<String> tagArray = new ArrayList<>();
		try(BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceresolver.getResourceResolver(serviceUser)) {
			ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
			Optional<List<String>> tagsNulllCheck = Optional.ofNullable(tags);
			if(tagsNulllCheck.isPresent() && tagsNulllCheck.get().size()>0) {
				if(null!=resourceResolver) {
					TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
					if(null!=tagManager) {
						for(String tag:tagsNulllCheck.get()) {
							Tag tz = tagManager.resolve(tag);
							if(null!=tz) {
								tagArray.add(tz.getName());
							}
						}
					}
				}
			}
		} catch (IllegalArgumentException | LoginException e) {
			LOG.error("::::::: Exception in resolving tags in getTagNameList method of CommonUtils class ::::::: " , e);
		} catch (Exception e1) {
			LOG.error("::::::: Broad Exception in resolving tags in getTagNameList method of CommonUtils class ::::::: " , e1);
		} 
		LOG.debug("::::::: Exit from getTagList method of CommonUtils class :::::::");
		return tagArray;
	}
	
	/**
	 * This is a static utility method that takes an input date in the 
	 * timestamp format and return the date in Mon,Date,Year (Jan, 01, 1970) format.
	 * @param date
	 * @return date in the Mon,Date,Year (Jan, 01, 1970) format.
	 */
	public static String dateFormat(String date) {
		LOG.debug("::::::: Entered dateFormat Method of CommonUtils class :::::::"); 
		LOG.debug(" Date Before Formatting ::: " + date);
		date = StringUtils.substringBefore(date, "T");
		if(StringUtils.isNotBlank(date)) {
			try {
				LocalDate ld1 = LocalDate.parse(date, DateTimeFormatter.ofPattern(BEIOConstants.YYMMDD_FORMAT));
				LOG.debug("::::::: Exit from dateFormat Method of CommonUtils class :::::::");
				return ld1.format(DateTimeFormatter.ofPattern(BEIOConstants.MMMDDYYYY_FORMAT));
				
			}catch(DateTimeParseException dex) {
				LOG.error("::::::: Exception parsing Date in dateFormat Method of CommonUtils class :::::::" , dex);
			}
		}
		LOG.debug("::::::: Exit from dateFormat Method of CommonUtils class, returning empty :::::::");
		return StringUtils.EMPTY;
	}
	
	/**
	 * This is a static utility method that takes the path of an attachment
	 * in the content fragment and returns the name of the attachment.
	 * @param path
	 * @return Name of the Attachment
	 */
	public static String getAttachmentName(String path) {
		LOG.debug("::::::: Entered getAttachmentName Method of CommonUtils class :::::::");
		if(StringUtils.isNotBlank(path)) {
			path = StringUtils.substringAfterLast(path, BEIOConstants.SLASH);
			path = StringUtils.substringBeforeLast(path, BEIOConstants.DOT);
			return path;
		}
		LOG.debug("::::::: Exit from getAttachmentName Method of CommonUtils class :::::::");
		return StringUtils.EMPTY;
	}
	
	/**This is a static utility method to sort ProductPromoModel fragments based on 
	 * the priority set in their priority field.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static int productPromoSorter(ProductPromoModel p1, ProductPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	
	/** This is a static utility method to sort ProductNonPromoModel fragments based on 
	 * the priority set in their priority field.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static int productNonPromoSorter(ProductNonPromoModel p1, ProductNonPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	
	/** This is a static utility method to sort BEIOPromoModel fragments based on 
	 * the priority set in their priority field.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static int beioPromoSorter(BEIOPromoModel p1, BEIOPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	
	/** This is a static utility method to sort BEIONonPromoModel fragments based on 
	 * the priority set in their priority field.
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static int beioNonPromoSorter(BEIONonPromoModel p1, BEIONonPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	public static int medinfoPromoSorter(MedinfoPromoModel p1, MedinfoPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	
	public static int medinfoNonPromoSorter(MedinfoNonPromoModel p1, MedinfoNonPromoModel p2) {
		Integer i1 = null!=p1.getPriority()?p1.getPriority():65000;
		Integer i2 = null!=p2.getPriority()?p2.getPriority():65000;
		if(i1>i2) {
			return 1;
		}else if(i1<i2) {
			return -1;
		}
		return 0;
	}
	
	/** This is a static utility method containing the common functionality 
	 *  to sort Communications, Documents and Links
	 * @param date1
	 * @param date2
	 * @return 0 if published on the same date
	 * @return 1 if model2 is published after model1
	 * @return -1 if model2 is published before model1
	 */
	private static int dateSorter(String date1, String date2) {
		LOG.trace("::::::: Entered dateSorter Method of CommonUtils class :::::::"); 
		if(StringUtils.isNotBlank(date1)&&StringUtils.isNotBlank(date2)) {
			
			LocalDate l1 = LocalDate.parse(date1,DateTimeFormatter.ofPattern(BEIOConstants.MMMDDYYYY_FORMAT));
			LocalDate l2 = LocalDate.parse(date2,DateTimeFormatter.ofPattern(BEIOConstants.MMMDDYYYY_FORMAT));
			return l2.compareTo(l1);
		}
		LOG.trace("::::::: Exit from dateSorter Method of CommonUtils class :::::::"); 
		return 0;
	}
	
	/** This is a static utility method to sort content fragments based on the publish date. 
	 * @param m1
	 * @param m2
	 * @return
	 */
	public static int fragmentsDateSorter(BaseModel m1, BaseModel m2) {
		return dateSorter(m1.getPublishDate(), m2.getPublishDate());
	}
	
	/** This is a static utility method used to compare the time stamps of the content fragments.
	 * @param date1
	 * @param date2
	 * @return 0 if timestamps are same.
	 * @return 1 if date2 is greater than date1.
	 * @return -1 if date2 is lesser than date1.
	 */
	public static int compareTimeStamps(String date1,String date2) {
		LOG.trace("::::::: Entered compareTimeStamps Method of CommonUtils class :::::::");
		if(StringUtils.isNotBlank(date1) && StringUtils.isNotBlank(date2)){
			LocalDateTime d1 = LocalDateTime.parse(date1, DateTimeFormatter.ISO_INSTANT);
			LocalDateTime d2 = LocalDateTime.parse(date2, DateTimeFormatter.ISO_INSTANT);
			return d2.compareTo(d1);
		}
		LOG.trace("::::::: Exit from compareTimeStamps Method of CommonUtils class :::::::"); 
		return 0;
	}
	
	/** This is a static utility method to replicate content fragments.
	 * @param path
	 * @param autoClosingResourceResolverFactory
	 * @param replicator
	 * @param agentManager
	 * @param replicationAgents
	 */
	public static void replicateNode(String path,BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory,Replicator replicator,AgentManager agentManager,String[] replicationAgents) {
		LOG.debug(":::::::::: Entered replicateNode Method of CommonUtils Class :::::::::: ");
		if(null!=replicator && null!= agentManager && null!=replicationAgents && replicationAgents.length>0) {
			ResourceReplicator rs= (resourcePath, autoClosingResolverFactory,resourceRreplicator,repAgentManager,availablereplicationAgents)->{
				try (BEIOAutoClosingResourceResolver autoClosingResolver = autoClosingResourceResolverFactory.getResourceResolver(BEIOConstants.BEIO_ADMIN)){
					ResourceResolver resourceResolver = autoClosingResolver.getResurceResolver();
					Resource contentNode = resourceResolver.resolve(resourcePath);
					if(CommonUtils.checkResource(contentNode)) {
						contentNode.adaptTo(ModifiableValueMap.class).put(BEIOConstants.PUBLISH_DATE, Calendar.getInstance());
						try {
							resourceResolver.adaptTo(Session.class).save();
						} catch (RepositoryException e) {
							LOG.error(":::::::::: Exception while saving publish date in replicateNode Method of CommonUtils :::::::::: " , e);
						}
					}
					final ReplicationOptions replicationOptions = new ReplicationOptions();
					replicationOptions.setSynchronous(true);
					replicationOptions.setSuppressVersions(false);
					replicationOptions.setSuppressStatusUpdate(false);
					for(Agent agent: repAgentManager.getAgents().values()) {
						LOG.info("agent ::::" + agent.getId() + " :::: " + agent.getConfiguration().getAgentId() + " " + agent.getConfiguration().getName());
					}
					for(Agent agent: repAgentManager.getAgents().values()) {
						if(agent.isEnabled() && agent.isValid() && Arrays.asList(availablereplicationAgents).contains(agent.getId())) {
							//Arrays.asList(availablereplicationAgents).contains(agent.getId());
							try {
								resourceRreplicator.checkPermission(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, resourcePath);
								replicationOptions.setFilter(new AgentIdFilter(agent.getId()));
								resourceRreplicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, resourcePath);
							} catch (ReplicationException e) {
								LOG.error(":::::::::: Exception in replicating node in replicateNode Method of CommonUtils :::::::::: " , e);
							}
						}
					}
				} catch(LoginException | IllegalArgumentException e) {
					LOG.error(" :::::::::: Exception in replicating node " + path + " in replicateNode method of CommonUtils class :::::::::: " , e);
				} catch(Exception e) {
					LOG.error(" :::::::::: Broad Exception in replicating node " + path + " in replicateNode method of CommonUtils class :::::::::: " , e);
				}
			};
			rs.replicateResource(path,autoClosingResourceResolverFactory, replicator, agentManager, replicationAgents);
		}
		LOG.debug(":::::::::: Exit from replicateNode Method of CommonUtils Class :::::::::: ");
	}
	
	
	/** This is a static utility method that adapts the Resource Object passed to 
	 * GenericListingComponentModel object and returns it.
	 * @param currentResource
	 * @return GenericListingComponentModel
	 */
	public static List<GenericListingComponentModel> getGenericListingComponentModel(Resource currentResource) {
		LOG.debug(":::::::::: Entered GenericListingComponentModel Method of CommonUtils class :::::::::: ");
		List<GenericListingComponentModel> genericListingList = new ArrayList<>();
		if(CommonUtils.checkResource(currentResource)) {
			Resource root = currentResource.getChild(BEIOConstants.ROOT);
			if(CommonUtils.checkResource(root)) {
				Iterator<Resource> contentModels = root.getChildren().iterator();
				if(null!=contentModels) {
					while(contentModels.hasNext()) {
						Resource contentModel = contentModels.next();
						if(CommonUtils.checkResource(contentModel)) {
							try {
								GenericListingComponentModel genericListingModel = contentModel.adaptTo(GenericListingComponentModel.class);
								if(null!=genericListingModel) {
									genericListingList.add(contentModel.adaptTo(GenericListingComponentModel.class));
								}
							}catch(Exception ex) {
								LOG.error(":::::::::: Exception occurred while adapting to GenericListingModel in GenericListingModel Method of CommonUtils class :::::::::: " , ex);
							}
						}
					}
				}
			}
		}
		LOG.debug(":::::::::: Exit from GenericListingComponentModel Method of CommonUtils class :::::::::: ");
		return genericListingList;
	}
	
	/** This is a static utility method that extracts the Country and Language values
	 * from the url passed into it.
	 * @param path
	 * @return
	 */
	public static Map<String,String> getDetails(String path)
	{
		Map<String,String> obj = new HashMap<String,String>();
		String[] array = path.split("/");
		obj.put(BEIOConstants.APP_NAME, StringUtils.upperCase(array[2]));
		obj.put(BEIOConstants.COUNTRY, array[3]);
		obj.put(BEIOConstants.LANGUAGE, array[4]);
		return obj;
	}
}