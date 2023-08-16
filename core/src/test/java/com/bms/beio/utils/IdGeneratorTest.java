package com.bms.beio.utils;

import org.junit.Assert;
import org.junit.Test;


public class IdGeneratorTest {

	IdGenerator idgenerator=new IdGenerator();
	
	@SuppressWarnings("static-access")
	@Test
	public void test()
	{
		Assert.assertEquals("185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969", idgenerator.generateUniqueID("Hello"));
	}
}
