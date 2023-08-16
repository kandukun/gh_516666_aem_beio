package com.bms.beio.slingmodels;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MedinfoPromoModelTest {

	MedinfoPromoModel medinfoPromoModel = new MedinfoPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		medinfoPromoModel.setTitle("compoundName");
		medinfoPromoModel.setCoverimage("coverimage");
		medinfoPromoModel.setSubTitle("subTitle");
		medinfoPromoModel.setDetailedContent("detailedContent");
		medinfoPromoModel.setAttachmenttext1("attachmenttext1");
		medinfoPromoModel.setAttachmentpath1("attachmentpath1");
		medinfoPromoModel.setAttachmenttext2("attachmenttext2");
		medinfoPromoModel.setAttachmentpath2("attachmentpath2");
		medinfoPromoModel.setVideoid("videoid");
		medinfoPromoModel.setPriority(3);
		medinfoPromoModel.setDisclaimer("disclaimer");
		medinfoPromoModel.setKeyword("keyword");
		list.add("list1");
		list.add("list2");
		medinfoPromoModel.setTherapeuticarea(list);
		medinfoPromoModel.setIndication(list);
		medinfoPromoModel.setTags(list);
		medinfoPromoModel.setProducts(list);
		medinfoPromoModel.setDownloadable("true");
		medinfoPromoModel.setShareable("true");
		medinfoPromoModel.setType("Medinfo");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(medinfoPromoModel);
		Assert.assertEquals("Medinfo", medinfoPromoModel.getType());
		Assert.assertEquals("compoundName", medinfoPromoModel.getTitle());
		Assert.assertEquals("coverimage", medinfoPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", medinfoPromoModel.getSubTitle());
		Assert.assertEquals("disclaimer",medinfoPromoModel.getDisclaimer());
		Assert.assertEquals("keyword", medinfoPromoModel.getKeyword());
		Assert.assertEquals("detailedContent", medinfoPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", medinfoPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", medinfoPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", medinfoPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", medinfoPromoModel.getAttachmentpath2());
		Assert.assertEquals("videoid", medinfoPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, medinfoPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, medinfoPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, medinfoPromoModel.getIndication());
		Assert.assertEquals(list, medinfoPromoModel.getTags());
		Assert.assertEquals(list, medinfoPromoModel.getProducts());
		Assert.assertEquals(list, medinfoPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, medinfoPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, medinfoPromoModel.getProductsTagNames());
		Assert.assertEquals("true", medinfoPromoModel.getDownloadable());
		Assert.assertEquals("true", medinfoPromoModel.getShareable());
		
		medinfoPromoModel.setAttachmenttext1("");
		medinfoPromoModel.setAttachmenttext2("");
		
		Assert.assertEquals("", medinfoPromoModel.getAttachmenttext1());
		Assert.assertEquals("", medinfoPromoModel.getAttachmenttext2());
		
	}
}
