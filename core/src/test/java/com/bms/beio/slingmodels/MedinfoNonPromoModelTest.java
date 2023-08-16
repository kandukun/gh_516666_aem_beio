package com.bms.beio.slingmodels;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MedinfoNonPromoModelTest {

	MedinfoNonPromoModel medinfoNonPromoModel = new MedinfoNonPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		medinfoNonPromoModel.setTitle("compoundName");
		medinfoNonPromoModel.setCoverimage("coverimage");
		medinfoNonPromoModel.setSubTitle("subTitle");
		medinfoNonPromoModel.setDetailedContent("detailedContent");
		medinfoNonPromoModel.setAttachmenttext1("attachmenttext1");
		medinfoNonPromoModel.setAttachmentpath1("attachmentpath1");
		medinfoNonPromoModel.setAttachmenttext2("attachmenttext2");
		medinfoNonPromoModel.setAttachmentpath2("attachmentpath2");
		medinfoNonPromoModel.setVideoid("videoid");
		medinfoNonPromoModel.setPriority(3);
		medinfoNonPromoModel.setDisclaimer("disclaimer");
		medinfoNonPromoModel.setKeyword("keyword");
		list.add("list1");
		list.add("list2");
		medinfoNonPromoModel.setTherapeuticarea(list);
		medinfoNonPromoModel.setIndication(list);
		medinfoNonPromoModel.setTags(list);
		medinfoNonPromoModel.setProducts(list);
		medinfoNonPromoModel.setDownloadable("true");
		medinfoNonPromoModel.setShareable("true");
		medinfoNonPromoModel.setType("Medinfo");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(medinfoNonPromoModel);
		Assert.assertEquals("Medinfo", medinfoNonPromoModel.getType());
		Assert.assertEquals("compoundName", medinfoNonPromoModel.getTitle());
		Assert.assertEquals("coverimage", medinfoNonPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", medinfoNonPromoModel.getSubTitle());
		Assert.assertEquals("disclaimer",medinfoNonPromoModel.getDisclaimer());
		Assert.assertEquals("keyword", medinfoNonPromoModel.getKeyword());
		Assert.assertEquals("detailedContent", medinfoNonPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", medinfoNonPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", medinfoNonPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", medinfoNonPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", medinfoNonPromoModel.getAttachmentpath2());
		Assert.assertEquals("videoid", medinfoNonPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, medinfoNonPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, medinfoNonPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, medinfoNonPromoModel.getIndication());
		Assert.assertEquals(list, medinfoNonPromoModel.getTags());
		Assert.assertEquals(list, medinfoNonPromoModel.getProducts());
		Assert.assertEquals(list, medinfoNonPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, medinfoNonPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, medinfoNonPromoModel.getProductsTagNames());
		Assert.assertEquals("true", medinfoNonPromoModel.getDownloadable());
		Assert.assertEquals("true", medinfoNonPromoModel.getShareable());
		
		medinfoNonPromoModel.setAttachmenttext1("");
		medinfoNonPromoModel.setAttachmenttext2("");
		
		Assert.assertEquals("", medinfoNonPromoModel.getAttachmenttext1());
		Assert.assertEquals("", medinfoNonPromoModel.getAttachmenttext2());
		
	}
}
