package com.bms.beio.slingmodels;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.settings.SlingSettingsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class BaseModelTest {
	BaseModel baseModel  = new BaseModel();
	List<String> roles = new ArrayList<>();
	List<String> tags = new ArrayList<>();
	
	@Mock
	SlingSettingsService slingSettingsService;
	
	@Before
	public void setUp(){
		baseModel.setCfUniqueID("cfUniqueID");
		baseModel.setCreationDate("2018-08-25T15:44:25.249+05:30");
		baseModel.setPublishDate("2018-08-25T15:44:25.249+05:30");
		
		roles.add("role1");
		roles.add("role2");
		baseModel.setRole(roles);
		
		tags.add("tags1");
		tags.add("tags2");
		baseModel.setTags(tags);
		baseModel.setSlingService(slingSettingsService);
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(baseModel);
		Assert.assertEquals("cfUniqueID", baseModel.getCfUniqueID());
		Assert.assertEquals("2018-08-25T15:44:25.249+05:30", baseModel.getCreationDate());
		Assert.assertEquals("Aug 25, 2018", baseModel.getPublishDate());
		Assert.assertEquals(roles, baseModel.getRawRole());
		List<String> list = new ArrayList<>();
		Assert.assertEquals(list, baseModel.getRole());
		Assert.assertEquals(list, baseModel.getRoleTagNames());
		Assert.assertEquals(list, baseModel.getTagNames());
		Assert.assertEquals(list, baseModel.getTags());
		Assert.assertEquals(slingSettingsService, baseModel.getSlingService());
		baseModel.isPublish();
	}

}
