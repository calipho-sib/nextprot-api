package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;


public class SequenceVariantUtilsTest {

	@Test
	public void testGetIsoformNumber() {
		Integer value = SequenceVariantUtils.getIsoformNumber("WT1-iso4-p.Val380_Gly407del");
		Assert.assertEquals(Integer.valueOf(4), value);
	}

	
	@Test
	public void testGetIsoformNumberShouldReturnOptional() {
		Integer value = SequenceVariantUtils.getIsoformNumber("WT1-p.Val432Serfs*87");
		Assert.assertNull(value);
	}

}
