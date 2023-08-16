package com.bms.beio.slingmodels;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BEIONonPromoModelTest {
	
	BEIONonPromoModel beioNonPromoModel = new BEIONonPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		beioNonPromoModel.setType("Event");
		beioNonPromoModel.setTitle("compoundName");
		beioNonPromoModel.setCoverimage("coverimage");
		beioNonPromoModel.setSubTitle("subTitle");
		beioNonPromoModel.setDetailedContent("detailedContent");
		beioNonPromoModel.setAttachmenttext1("attachmenttext1");
		beioNonPromoModel.setAttachmentpath1("attachmentpath1");
		beioNonPromoModel.setAttachmenttext2("attachmenttext2");
		beioNonPromoModel.setAttachmentpath2("attachmentpath2");
		beioNonPromoModel.setAttachmenttext3("attachmenttext3");
		beioNonPromoModel.setAttachmentpath3("attachmentpath3");
		beioNonPromoModel.setAttachmenttext4("attachmenttext4");
		beioNonPromoModel.setAttachmentpath4("attachmentpath4");
		beioNonPromoModel.setAttachmenttext5("attachmenttext5");
		beioNonPromoModel.setAttachmentpath5("attachmentpath5");
		beioNonPromoModel.setVideoid("videoid");
		beioNonPromoModel.setPriority(3);
		list.add("list1");
		list.add("list2");
		beioNonPromoModel.setTherapeuticarea(list);
		beioNonPromoModel.setIndication(list);
		beioNonPromoModel.setTags(list);
		beioNonPromoModel.setProducts(list);
		beioNonPromoModel.setDownloadable("true");
		beioNonPromoModel.setShareable("true");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(beioNonPromoModel);
		Assert.assertEquals("Event", beioNonPromoModel.getType());
		Assert.assertEquals("compoundName", beioNonPromoModel.getTitle());
		Assert.assertEquals("coverimage", beioNonPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", beioNonPromoModel.getSubTitle());
		Assert.assertEquals("detailedContent", beioNonPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", beioNonPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", beioNonPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", beioNonPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", beioNonPromoModel.getAttachmentpath2());
		Assert.assertEquals("attachmenttext3", beioNonPromoModel.getAttachmenttext3());
		Assert.assertEquals("attachmentpath3", beioNonPromoModel.getAttachmentpath3());
		Assert.assertEquals("attachmenttext4", beioNonPromoModel.getAttachmenttext4());
		Assert.assertEquals("attachmentpath4", beioNonPromoModel.getAttachmentpath4());
		Assert.assertEquals("attachmenttext5", beioNonPromoModel.getAttachmenttext5());
		Assert.assertEquals("attachmentpath5", beioNonPromoModel.getAttachmentpath5());
		Assert.assertEquals("videoid", beioNonPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, beioNonPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, beioNonPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, beioNonPromoModel.getIndication());
		Assert.assertEquals(list, beioNonPromoModel.getTags());
		Assert.assertEquals(list, beioNonPromoModel.getProducts());
		Assert.assertEquals(list, beioNonPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, beioNonPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, beioNonPromoModel.getProductsTagNames());
		Assert.assertEquals("true", beioNonPromoModel.getDownloadable());
		Assert.assertEquals("true", beioNonPromoModel.getShareable());
		
		beioNonPromoModel.setAttachmenttext1("");
		beioNonPromoModel.setAttachmenttext2("");
		beioNonPromoModel.setAttachmenttext3("");
		beioNonPromoModel.setAttachmenttext4("");
		beioNonPromoModel.setAttachmenttext5("");
		Assert.assertEquals("", beioNonPromoModel.getAttachmenttext1());
		Assert.assertEquals("", beioNonPromoModel.getAttachmenttext2());
		Assert.assertEquals("", beioNonPromoModel.getAttachmenttext3());
		Assert.assertEquals("", beioNonPromoModel.getAttachmenttext4());
		Assert.assertEquals("", beioNonPromoModel.getAttachmenttext5());
	}

}
