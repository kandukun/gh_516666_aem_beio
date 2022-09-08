package com.bms.beio.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bms.beio.constants.BEIOConstants;
import com.bms.beio.resource.BEIOAutoClosingResourceResolver;
import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.bms.beio.slingmodels.BEIONonPromoModel;
import com.bms.beio.slingmodels.BEIOPromoModel;
import com.bms.beio.slingmodels.BaseModel;
import com.bms.beio.slingmodels.GenericListingComponentModel;
import com.bms.beio.slingmodels.ProductNonPromoModel;
import com.bms.beio.slingmodels.ProductPromoModel;
import com.bms.bmscorp.core.service.BMSDomainFactory;
import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentConfig;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CommonUtils.class, Pattern.class})
public class CommonUtilsTest {
	
	CommonUtils commonUtils = new CommonUtils();
	@Mock
	Resource resource;
	
	@Mock
	Resource root;
	
	@Mock
	Resource contentNode;
	
	@Mock
	Resource contentModel;
	
	@Mock
	ResourceResolver resolver;
	
	@Mock
	BMSDomainFactory domainFactory;
	
	@Mock 
	private SlingHttpServletRequest request;
	
	@Mock
	ValueMap valueMap;
	
	@Mock
	BEIOAutoClosingResourceResolverFactory autoClosingResourceresolver;
	
	@Mock
	TagManager tagManager;
	
	@Mock
	BEIOAutoClosingResourceResolver autoCloseResolver;
	
	@Mock
	Tag tag;
	
	@Mock
	ProductPromoModel p1;
	
	@Mock
	ProductPromoModel p2;
	
	@Mock
	ProductNonPromoModel nonpromo1;
	
	@Mock
	ProductNonPromoModel nonpromo2;
	
	@Mock
	BaseModel m1;
	
	@Mock
	BaseModel m2;
	
	@Mock
	BEIOPromoModel beiop1;
	
	@Mock
	BEIOPromoModel beiop2;
	
	@Mock
	BEIONonPromoModel beiononpromo1;
	
	@Mock
	BEIONonPromoModel beiononpromo2;
	
	@Mock
	Iterable<Resource> iterable;
	
	@Mock
	Iterator<Resource> contentModels;
	
	@Mock
	GenericListingComponentModel genericListingModel;
	
	@Mock
	Replicator replicator;
	
	@Mock
	AgentManager agentManager;
	
	@Mock
	ModifiableValueMap modifiablevaluemap;
	
	@Mock
	Session session;
	
	@Mock
	Agent agent;
	
	@Mock
	AgentConfig agentconfig;
	
	String[] replicationAgents= {"agent 1","agent 2"};
	
	String path="/content/beio/image.jpeg";
	
	@Before
	public void setUp() throws IllegalArgumentException, LoginException {
		resource = mock(Resource.class);
		contentNode = mock(Resource.class);
		root = mock(Resource.class);
		contentModel=mock(Resource.class);
		genericListingModel=mock(GenericListingComponentModel.class);
		resolver = mock(ResourceResolver.class);
		domainFactory = mock(BMSDomainFactory.class);
		request = mock(SlingHttpServletRequest.class);
		valueMap=mock(ValueMap.class);
		autoClosingResourceresolver=mock(BEIOAutoClosingResourceResolverFactory.class);
		autoCloseResolver=mock(BEIOAutoClosingResourceResolver.class);
		tagManager = mock(TagManager.class);
		tag = mock(Tag.class);
		p1=mock(ProductPromoModel.class);
		p2=mock(ProductPromoModel.class);
		nonpromo1=mock(ProductNonPromoModel.class);
		nonpromo2=mock(ProductNonPromoModel.class);
		Boolean isPublish = false;
		when(domainFactory.getEndUserExternalLink("/content/beio/us/en_us/home/testpage.html", isPublish, request)).thenReturn("/content/beio/us/en_us/home/testpage.html");
		when(resource.getValueMap()).thenReturn(valueMap);
		when(valueMap.containsKey("Title")).thenReturn(true);
		when(valueMap.get("Title",String.class)).thenReturn("Sample Text");
		agentManager=mock(AgentManager.class);
		replicator=mock(Replicator.class);
		modifiablevaluemap=mock(ModifiableValueMap.class);
		agent=mock(Agent.class);
		agentconfig=mock(AgentConfig.class);
		session=mock(Session.class);
	}

	@Test
	public void testCheckResource() {
		CommonUtils.checkResource(resource);
	}
	
	@Test
	public void testCalculateProperFileSize() {
		Assert.assertEquals("4.88KB", CommonUtils.calculateProperFileSize(5000));
	}
	
	@Test
	public void testGetProperty() {
		Assert.assertEquals("Sample Text", CommonUtils.getProperty(resource, "Title"));
		Assert.assertEquals("", CommonUtils.getProperty(null, "Title"));
		CommonUtils.getProperty(resource, "");
	}
	
	@Test
	public void testRemoveTag() {
		Assert.assertEquals("Test  content", CommonUtils.removeTag("Test tag1 content", "tag1"));
	}
	
	@Test
	public void testGetHrefTextValue() throws URISyntaxException {
		Assert.assertEquals("Document desc <a href='/content/beio/us/en_us/home/testpage.html' title='' class='' target='_blank' aria-label=''>PageLInk</a>", CommonUtils.getHrefTextValue("Document desc <a href=\"/content/beio/us/en_us/home/testpage.html\" target=\"_blank\">PageLInk</a>", null, domainFactory, false, request));
		Assert.assertEquals("Document desc <a href='#content/beio/us/en_us/home/testpage.html' title='' class='' target='_blank' aria-label=''>PageLInk</a>", CommonUtils.getHrefTextValue("Document desc <a href=\"#content/beio/us/en_us/home/testpage.html\" target=\"_blank\">PageLInk</a>", null, domainFactory, false, request));
		Assert.assertEquals("Document desc <a href='http://google.com' title='' class='' target='_blank' aria-label=''>PageLInk</a>", CommonUtils.getHrefTextValue("Document desc <a href=\"http://google.com\" target=\"_blank\">PageLInk</a>", null, domainFactory, false, request));
		Assert.assertEquals("Document desc", CommonUtils.getHrefTextValue("Document desc", null, domainFactory, false, request));
		CommonUtils.getHrefTextValue("", "",domainFactory, true, request);
	}
	
	@Test
	public void testGetHrefValue() throws URISyntaxException{
		Assert.assertEquals("<a href='http://localhost:4502/content/dam/beio' title='' class='' target='' aria-label=''></a>",CommonUtils.getHrefValue("http://localhost:4502/content/dam/beio/", "", "", "", "", "", "", request, domainFactory,true));
		Assert.assertEquals("<a href='mailto:' title='' class='' target='' aria-label=''   ></a>",CommonUtils.getHrefValue("mailto:", "", "", "", "", "", "", request, domainFactory,true));
		Assert.assertEquals(null, CommonUtils.getHrefValue(null, "", "", "", "", "", "", request, domainFactory,true));
	}
	
	@Test
	public void testGetImgTextValue() throws URISyntaxException {
		Assert.assertEquals("<p>Link description</p><p><img src='null'></img></p>", CommonUtils.getImgTextValue("<p>Link description</p><p><img src=\"/content/dam/breadcrumb.PNG\" data-assetref=\"breadcrumb-1537336960774\"></p>", null, domainFactory, false, request));
		Assert.assertEquals("<p>Link description</p><p><img src='http://google.com/image.PNG'></img></p>", CommonUtils.getImgTextValue("<p>Link description</p><p><img src=\"http://google.com/image.PNG\" data-assetref=\"breadcrumb-1537336960774\"></p>", null, domainFactory, false, request));
		Assert.assertEquals("Link description", CommonUtils.getImgTextValue("Link description", null, domainFactory, false, request));
		CommonUtils.getImgTextValue("", "", domainFactory, true, request);
	}
	
	@Test
	public void testGetImgValue() throws URISyntaxException{
		Assert.assertEquals("<img src='http://localhost:4502/content/dam/beio'></img>", CommonUtils.getImgValue("http://localhost:4502/content/dam/beio/", request, domainFactory, true));
		Assert.assertEquals(null, CommonUtils.getImgValue(null, request, domainFactory, true));
	}
	
	@Test
	public void testGetTagList() throws IllegalArgumentException, LoginException {
		List<String> tags = new ArrayList<>();
		tags.add("tag1");
		when(autoClosingResourceresolver.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoCloseResolver);
		when(autoCloseResolver.getResurceResolver()).thenReturn(resolver);
		when(resolver.adaptTo(TagManager.class)).thenReturn(tagManager);
		when(tagManager.resolve("tag1")).thenReturn(tag);
		when(tag.getTitle()).thenReturn("Tag1 title");
		List<String> tagsTitle = new ArrayList<>();
		tagsTitle.add("Tag1 title");
		List<String> tagsTitleEmpty = new ArrayList<>();
		Assert.assertEquals(tagsTitle, CommonUtils.getTagList(tags , autoClosingResourceresolver,BEIOConstants.BEIO_ADMIN));
		Assert.assertEquals(tagsTitleEmpty, CommonUtils.getTagList(tags , null,BEIOConstants.BEIO_ADMIN));
	}
	
	@Test
	public void testGetTagNameList() throws IllegalArgumentException, LoginException {
		List<String> tags = new ArrayList<>();
		tags.add("tag1");
		when(autoClosingResourceresolver.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoCloseResolver);
		when(autoCloseResolver.getResurceResolver()).thenReturn(resolver);
		when(resolver.adaptTo(TagManager.class)).thenReturn(tagManager);
		when(tagManager.resolve("tag1")).thenReturn(tag);
		when(tag.getName()).thenReturn("Tag1 name");
		List<String> tagsName = new ArrayList<>();
		tagsName.add("Tag1 name");
		List<String> tagsNameEmpty = new ArrayList<>();
		Assert.assertEquals(tagsName, CommonUtils.getTagNameList(tags , autoClosingResourceresolver,BEIOConstants.BEIO_ADMIN));
		Assert.assertEquals(tagsNameEmpty, CommonUtils.getTagNameList(tags , null,BEIOConstants.BEIO_ADMIN));
	}
	
	@Test
	public void testDateFormat() {
		Assert.assertEquals("Aug 25, 2018", CommonUtils.dateFormat("2018-08-25T15:44:25.249+05:30"));
		Assert.assertEquals("", CommonUtils.dateFormat("2018/08/25T15:44:25.249+05:30"));
	}
	
	@Test
	public void testGetAttachmentName() {
		Assert.assertEquals("image", CommonUtils.getAttachmentName("/content/dam/beio/image.jpg"));
		Assert.assertEquals("", CommonUtils.getAttachmentName(""));
	}
	
	@Test
	public void testProductPromoSorter()
	{
		when(p1.getPriority()).thenReturn(2);
		when(p2.getPriority()).thenReturn(1);
		Assert.assertEquals(1, CommonUtils.productPromoSorter(p1, p2));
		when(p1.getPriority()).thenReturn(1);
		when(p2.getPriority()).thenReturn(2);
		Assert.assertEquals(-1, CommonUtils.productPromoSorter(p1, p2));
		when(p1.getPriority()).thenReturn(null);
		when(p2.getPriority()).thenReturn(null);
		Assert.assertEquals(0, CommonUtils.productPromoSorter(p1, p2));	
	}
	
	@Test
	public void testProductNonPromoSorter()
	{
		when(nonpromo1.getPriority()).thenReturn(2);
		when(nonpromo2.getPriority()).thenReturn(1);
		Assert.assertEquals(1, CommonUtils.productNonPromoSorter(nonpromo1, nonpromo2));
		when(nonpromo1.getPriority()).thenReturn(1);
		when(nonpromo2.getPriority()).thenReturn(2);
		Assert.assertEquals(-1, CommonUtils.productNonPromoSorter(nonpromo1, nonpromo2));
		when(nonpromo1.getPriority()).thenReturn(null);
		when(nonpromo2.getPriority()).thenReturn(null);
		Assert.assertEquals(0, CommonUtils.productNonPromoSorter(nonpromo1, nonpromo2));	
	}
	
	@Test
	public void testBeioPromoSorter()
	{
		when(beiop1.getPriority()).thenReturn(2);
		when(beiop2.getPriority()).thenReturn(1);
		Assert.assertEquals(1, CommonUtils.beioPromoSorter(beiop1, beiop2));
		when(beiop1.getPriority()).thenReturn(1);
		when(beiop2.getPriority()).thenReturn(2);
		Assert.assertEquals(-1, CommonUtils.beioPromoSorter(beiop1, beiop2));
		when(beiop1.getPriority()).thenReturn(null);
		when(beiop2.getPriority()).thenReturn(null);
		Assert.assertEquals(0, CommonUtils.beioPromoSorter(beiop1, beiop2));		
	}
	
	@Test
	public void testBeioNonPromoSorter()
	{
		when(beiononpromo1.getPriority()).thenReturn(2);
		when(beiononpromo2.getPriority()).thenReturn(1);
		Assert.assertEquals(1, CommonUtils.beioNonPromoSorter(beiononpromo1, beiononpromo2));
		when(beiononpromo1.getPriority()).thenReturn(1);
		when(beiononpromo2.getPriority()).thenReturn(2);
		Assert.assertEquals(-1, CommonUtils.beioNonPromoSorter(beiononpromo1, beiononpromo2));
		when(beiononpromo1.getPriority()).thenReturn(null);
		when(beiononpromo2.getPriority()).thenReturn(null);
		Assert.assertEquals(0, CommonUtils.beioNonPromoSorter(beiononpromo1, beiononpromo2));	
	}
	
	@Test
	public void getHrefTextValue() throws URISyntaxException {
		/*PowerMockito.mockStatic(Pattern.class);
		when(Pattern.compile("\\s*(?i)id\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern);
		when(Pattern.compile("<img([^>]*[^/])>")).thenReturn(pattern1);
		when(Pattern.compile("(?i)<a([^>]+)>(.*?)</a>")).thenReturn(pattern1);
		when(Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern2);
		when(Pattern.compile("<a[^>]*>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE)).thenReturn(pattern1);
		when(Pattern.compile("\\s*(?i)title\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern);
		when(Pattern.compile("\\s*(?i)target\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern);
		when(Pattern.compile("\\s*(?i)download\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern);
		when(Pattern.compile("\\s*(?i)class\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))")).thenReturn(pattern);
		when(pattern.matcher("Document")).thenReturn(matcher);
		when(pattern1.matcher("Document")).thenReturn(matcher1);
		when(pattern2.matcher("Document")).thenReturn(matcher2);
		when(matcher.group(0)).thenReturn("url");
		when(matcher1.group(0)).thenReturn("url");
		when(matcher2.group(0)).thenReturn("#url");
		when(matcher.group(1)).thenReturn("url");
		when(matcher1.group(1)).thenReturn("url");
		when(matcher2.group(1)).thenReturn("#url");
		when(pattern.matcher("url")).thenReturn(matcher);
		when(pattern1.matcher("url")).thenReturn(matcher1);
		when(pattern2.matcher("url")).thenReturn(matcher2);
		when(matcher.find()).thenReturn(true);
		when(matcher1.find()).thenReturn(true,false);
		when(matcher2.find()).thenReturn(true);*/
		CommonUtils.getHrefTextValue("Document", null, domainFactory, false, request);		
	}
	
	@Test
	public void testFragmentsDateSorter()
	{
		CommonUtils.fragmentsDateSorter(m1, m2);
		m1=mock(BaseModel.class);
		m2=mock(BaseModel.class);
		when(m1.getPublishDate()).thenReturn("Dec 12, 2018");
		when(m2.getPublishDate()).thenReturn("May 05, 1993");
		CommonUtils.fragmentsDateSorter(m1, m2);
	}
	
	@Test
	public void testgetGenericListingComponentModel()
	{
		when(resource.getChild(BEIOConstants.ROOT)).thenReturn(root);
		when(root.getChildren()).thenReturn(iterable);
		when(iterable.iterator()).thenReturn(contentModels);
		when(contentModels.hasNext()).thenReturn(true).thenReturn(false);
		when(contentModels.next()).thenReturn(contentModel);
		when(contentModel.adaptTo(GenericListingComponentModel.class)).thenReturn(genericListingModel);
		CommonUtils.getGenericListingComponentModel(resource);
	}
	
	@Test
	public void testReplicateNode() throws IllegalArgumentException, LoginException
	{
		Map<String, Agent> agentmap=new HashMap<>();
		agentmap.put("agent 1", agent);
		when(autoClosingResourceresolver.getResourceResolver(BEIOConstants.BEIO_ADMIN)).thenReturn(autoCloseResolver);
		when(autoCloseResolver.getResurceResolver()).thenReturn(resolver);
		when(resolver.resolve(path)).thenReturn(contentNode);
		when(contentNode.adaptTo(ModifiableValueMap.class)).thenReturn(modifiablevaluemap);
		when(resolver.adaptTo(Session.class)).thenReturn(session);
		when(agentManager.getAgents()).thenReturn(agentmap);
		when(agent.getId()).thenReturn("agent 1");
		when(agent.getConfiguration()).thenReturn(agentconfig);
		when(agent.isValid()).thenReturn(true);
		when(agent.isEnabled()).thenReturn(true);
		when(agentconfig.getAgentId()).thenReturn("agent 1");
		when(agentconfig.getName()).thenReturn("agentName");	
		CommonUtils.replicateNode(path, autoClosingResourceresolver, replicator, agentManager, replicationAgents);
	}
	
	@Test
	public void testReplicateNodeCatchBlock()
	{
		CommonUtils.replicateNode(path, autoClosingResourceresolver, replicator, agentManager, replicationAgents);
	}
	
	@Test
	public void testGetDetails()
	{
		CommonUtils.getDetails("/content/dam/beio/ar/es_ar/promo");
	}
}
