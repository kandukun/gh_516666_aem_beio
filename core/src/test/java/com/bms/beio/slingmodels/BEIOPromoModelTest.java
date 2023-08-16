package com.bms.beio.slingmodels;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BEIOPromoModelTest {
	
	BEIOPromoModel beioPromoModel = new BEIOPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		beioPromoModel.setType("Event");
		beioPromoModel.setTitle("compoundName");
		beioPromoModel.setCoverimage("coverimage");
		beioPromoModel.setSubTitle("subTitle");
		beioPromoModel.setDetailedContent("detailedContent");
		beioPromoModel.setAttachmenttext1("attachmenttext1");
		beioPromoModel.setAttachmentpath1("attachmentpath1");
		beioPromoModel.setAttachmenttext2("attachmenttext2");
		beioPromoModel.setAttachmentpath2("attachmentpath2");
		beioPromoModel.setAttachmenttext3("attachmenttext3");
		beioPromoModel.setAttachmentpath3("attachmentpath3");
		beioPromoModel.setAttachmenttext4("attachmenttext4");
		beioPromoModel.setAttachmentpath4("attachmentpath4");
		beioPromoModel.setAttachmenttext5("attachmenttext5");
		beioPromoModel.setAttachmentpath5("attachmentpath5");
		beioPromoModel.setVideoid("videoid");
		beioPromoModel.setPriority(3);
		list.add("list1");
		list.add("list2");
		beioPromoModel.setTherapeuticarea(list);
		beioPromoModel.setIndication(list);
		beioPromoModel.setTags(list);
		beioPromoModel.setProducts(list);
		beioPromoModel.setDownloadable("true");
		beioPromoModel.setShareable("true");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(beioPromoModel);
		Assert.assertEquals("Event", beioPromoModel.getType());
		Assert.assertEquals("compoundName", beioPromoModel.getTitle());
		Assert.assertEquals("coverimage", beioPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", beioPromoModel.getSubTitle());
		Assert.assertEquals("detailedContent", beioPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", beioPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", beioPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", beioPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", beioPromoModel.getAttachmentpath2());
		Assert.assertEquals("attachmenttext3", beioPromoModel.getAttachmenttext3());
		Assert.assertEquals("attachmentpath3", beioPromoModel.getAttachmentpath3());
		Assert.assertEquals("attachmenttext4", beioPromoModel.getAttachmenttext4());
		Assert.assertEquals("attachmentpath4", beioPromoModel.getAttachmentpath4());
		Assert.assertEquals("attachmenttext5", beioPromoModel.getAttachmenttext5());
		Assert.assertEquals("attachmentpath5", beioPromoModel.getAttachmentpath5());
		Assert.assertEquals("videoid", beioPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, beioPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, beioPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, beioPromoModel.getIndication());
		Assert.assertEquals(list, beioPromoModel.getTags());
		Assert.assertEquals(list, beioPromoModel.getProducts());
		Assert.assertEquals(list, beioPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, beioPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, beioPromoModel.getProductsTagNames());
		Assert.assertEquals("true", beioPromoModel.getDownloadable());
		Assert.assertEquals("true", beioPromoModel.getShareable());
		
		beioPromoModel.setAttachmenttext1("");
		beioPromoModel.setAttachmenttext2("");
		beioPromoModel.setAttachmenttext3("");
		beioPromoModel.setAttachmenttext4("");
		beioPromoModel.setAttachmenttext5("");
		Assert.assertEquals("", beioPromoModel.getAttachmenttext1());
		Assert.assertEquals("", beioPromoModel.getAttachmenttext2());
		Assert.assertEquals("", beioPromoModel.getAttachmenttext3());
		Assert.assertEquals("", beioPromoModel.getAttachmenttext4());
		Assert.assertEquals("", beioPromoModel.getAttachmenttext5());
	}

}
