package com.bms.beio.slingmodels;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProductPromoModelTest {
	ProductPromoModel productPromoModel = new ProductPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		productPromoModel.setBrandName("compoundName");
		productPromoModel.setCoverimage("coverimage");
		productPromoModel.setSubTitle("subTitle");
		productPromoModel.setDetailedContent("detailedContent");
		productPromoModel.setAttachmenttext1("attachmenttext1");
		productPromoModel.setAttachmentpath1("attachmentpath1");
		productPromoModel.setAttachmenttext2("attachmenttext2");
		productPromoModel.setAttachmentpath2("attachmentpath2");
		productPromoModel.setAttachmenttext3("attachmenttext3");
		productPromoModel.setAttachmentpath3("attachmentpath3");
		productPromoModel.setAttachmenttext4("attachmenttext4");
		productPromoModel.setAttachmentpath4("attachmentpath4");
		productPromoModel.setAttachmenttext5("attachmenttext5");
		productPromoModel.setAttachmentpath5("attachmentpath5");
		productPromoModel.setVideoid("videoid");
		productPromoModel.setPriority(3);
		list.add("list1");
		list.add("list2");
		productPromoModel.setTherapeuticarea(list);
		productPromoModel.setIndication(list);
		productPromoModel.setProducts(list);
		productPromoModel.setDownloadable("true");
		productPromoModel.setShareable("true");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(productPromoModel);
		Assert.assertEquals("compoundName", productPromoModel.getBrandName());
		Assert.assertEquals("coverimage", productPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", productPromoModel.getSubTitle());
		Assert.assertEquals("detailedContent", productPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", productPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", productPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", productPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", productPromoModel.getAttachmentpath2());
		Assert.assertEquals("attachmenttext3", productPromoModel.getAttachmenttext3());
		Assert.assertEquals("attachmentpath3", productPromoModel.getAttachmentpath3());
		Assert.assertEquals("attachmenttext4", productPromoModel.getAttachmenttext4());
		Assert.assertEquals("attachmentpath4", productPromoModel.getAttachmentpath4());
		Assert.assertEquals("attachmenttext5", productPromoModel.getAttachmenttext5());
		Assert.assertEquals("attachmentpath5", productPromoModel.getAttachmentpath5());
		Assert.assertEquals("videoid", productPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, productPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, productPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, productPromoModel.getIndication());
		Assert.assertEquals(list, productPromoModel.getProducts());
		Assert.assertEquals(list, productPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, productPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, productPromoModel.getProductsTagNames());
		Assert.assertEquals("true", productPromoModel.getDownloadable());
		Assert.assertEquals("true", productPromoModel.getShareable());
	}
}
