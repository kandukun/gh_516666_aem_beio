package com.bms.beio.slingmodels;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProductNonPromoModelTest {
	ProductNonPromoModel productNonPromoModel = new ProductNonPromoModel();
	List<String> list = new ArrayList<>();
	
	@Before
	public void setUp(){
		productNonPromoModel.setCompoundName("compoundName");
		productNonPromoModel.setCoverimage("coverimage");
		productNonPromoModel.setSubTitle("subTitle");
		productNonPromoModel.setDetailedContent("detailedContent");
		productNonPromoModel.setAttachmenttext1("attachmenttext1");
		productNonPromoModel.setAttachmentpath1("attachmentpath1");
		productNonPromoModel.setAttachmenttext2("attachmenttext2");
		productNonPromoModel.setAttachmentpath2("attachmentpath2");
		productNonPromoModel.setAttachmenttext3("attachmenttext3");
		productNonPromoModel.setAttachmentpath3("attachmentpath3");
		productNonPromoModel.setAttachmenttext4("attachmenttext4");
		productNonPromoModel.setAttachmentpath4("attachmentpath4");
		productNonPromoModel.setAttachmenttext5("attachmenttext5");
		productNonPromoModel.setAttachmentpath5("attachmentpath5");
		productNonPromoModel.setVideoid("videoid");
		productNonPromoModel.setPriority(3);
		list.add("list1");
		list.add("list2");
		productNonPromoModel.setTherapeuticarea(list);
		productNonPromoModel.setIndication(list);
		productNonPromoModel.setProducts(list);
		productNonPromoModel.setDownloadable("true");
		productNonPromoModel.setShareable("true");
	}
	
	@Test
	public void linkjsonresults(){
		Assert.assertNotNull(productNonPromoModel);
		Assert.assertEquals("compoundName", productNonPromoModel.getCompoundName());
		Assert.assertEquals("coverimage", productNonPromoModel.getCoverimage());
		Assert.assertEquals("subTitle", productNonPromoModel.getSubTitle());
		Assert.assertEquals("detailedContent", productNonPromoModel.getDetailedContent());
		Assert.assertEquals("attachmenttext1", productNonPromoModel.getAttachmenttext1());
		Assert.assertEquals("attachmentpath1", productNonPromoModel.getAttachmentpath1());
		Assert.assertEquals("attachmenttext2", productNonPromoModel.getAttachmenttext2());
		Assert.assertEquals("attachmentpath2", productNonPromoModel.getAttachmentpath2());
		Assert.assertEquals("attachmenttext3", productNonPromoModel.getAttachmenttext3());
		Assert.assertEquals("attachmentpath3", productNonPromoModel.getAttachmentpath3());
		Assert.assertEquals("attachmenttext4", productNonPromoModel.getAttachmenttext4());
		Assert.assertEquals("attachmentpath4", productNonPromoModel.getAttachmentpath4());
		Assert.assertEquals("attachmenttext5", productNonPromoModel.getAttachmenttext5());
		Assert.assertEquals("attachmentpath5", productNonPromoModel.getAttachmentpath5());
		Assert.assertEquals("videoid", productNonPromoModel.getVideoid());
		Integer n = 3;
		Assert.assertEquals(n, productNonPromoModel.getPriority());
		List<String> list=new ArrayList<>();
		Assert.assertEquals(list, productNonPromoModel.getTherapeuticarea());
		Assert.assertEquals(list, productNonPromoModel.getIndication());
		Assert.assertEquals(list, productNonPromoModel.getProducts());
		Assert.assertEquals(list, productNonPromoModel.getTherapeuticareaTagNames());
		Assert.assertEquals(list, productNonPromoModel.getIndicationTagNames());
		Assert.assertEquals(list, productNonPromoModel.getProductsTagNames());
		Assert.assertEquals("true", productNonPromoModel.getDownloadable());
		Assert.assertEquals("true", productNonPromoModel.getShareable());
	}
}
