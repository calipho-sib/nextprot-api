package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;


public class SequenceVariantUtilsTest {

	@Test
	public void testGetIsoformNumber() {
		String value = SequenceVariantUtils.getIsoformName("WT1-iso4-p.Val380_Gly407del");
		Assert.assertEquals("4", value);
	}
	
	

	@Test
	public void testGetIsoformName() {
		String value = SequenceVariantUtils.getIsoformName("WT1-isoshort-p.Val380_Gly407del");
		Assert.assertEquals("short", value);
	}

	
	@Test
	public void testGetIsoformNumberShouldReturnOptional() {
		String value = SequenceVariantUtils.getIsoformName("WT1-p.Val432Serfs*87");
		Assert.assertNull(value);
	}

}
