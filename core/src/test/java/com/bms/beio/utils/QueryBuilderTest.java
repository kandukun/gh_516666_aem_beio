package com.bms.beio.utils;

import org.junit.Test;

import com.bms.beio.constants.BEIOConstants;

public class QueryBuilderTest {
	
	QueryBuilder querybuilder=new QueryBuilder();
	
	@SuppressWarnings("static-access")
	@Test
	public void test()
	{
		querybuilder.buildServletQuery(null, BEIOConstants.PRODUCTS_PROMO_MODEL, "2018-08-25T15:44:25.249+05:30", true);
		querybuilder.buildServletQuery(null, BEIOConstants.PRODUCTS_PROMO_MODEL, "2018-08-25T15:44:25.249+05:30", false);
		querybuilder.buildQueryToFetchFragments(null, BEIOConstants.PRODUCTS_PROMO_MODEL, "2018-08-25T15:44:25.249+05:30");
	}

}
