package com.bms.beio.slingmodels;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NewsModelTest {
	
	NewsModel newsModel=new NewsModel();
	
	@Before
	public void setUp()
	{
		newsModel.setCfUniqueID("cfUniqueID");
		newsModel.setCreationDate("2018-08-25T15:44:25.249+05:30");
		newsModel.setPublishDate("2018-08-25T15:44:25.249+05:30");
		newsModel.setCoverimage("coverimage");
		newsModel.setTitle("Title");
		newsModel.setSubTitle("subTitle");
		newsModel.setDetailedContent("detailedContent");
		newsModel.setAttachmenttext("attachmenttext");
		newsModel.setAttachmentpath("attachmentpath");
	}
	
	@Test
	public void test()
	{
		Assert.assertNotNull(newsModel);
		Assert.assertEquals("cfUniqueID", newsModel.getCfUniqueID());
		Assert.assertEquals("2018-08-25T15:44:25.249+05:30", newsModel.getCreationDate());
		Assert.assertEquals("2018-08-25T15:44:25.249+05:30", newsModel.getPublishDate());
		Assert.assertEquals("coverimage", newsModel.getCoverimage());
		Assert.assertEquals("Title", newsModel.getTitle());
		Assert.assertEquals("subTitle", newsModel.getSubTitle());
		Assert.assertEquals("detailedContent", newsModel.getDetailedContent());
		Assert.assertEquals("attachmenttext", newsModel.getAttachmenttext());
		Assert.assertEquals("attachmentpath", newsModel.getAttachmentpath());
		Assert.assertEquals("2018-08-25T15:44:25.249+05:30", newsModel.getSortingDate());
		newsModel.setPublishDate("");
		Assert.assertEquals("2018-08-25T15:44:25.249+05:30", newsModel.getSortingDate());
	}
}
