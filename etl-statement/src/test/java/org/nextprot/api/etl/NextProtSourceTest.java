package org.nextprot.api.etl;

import org.junit.Assert;
import org.junit.Test;

public class NextProtSourceTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfLowerCase() {

		NextProtSource.valueOf("gnomad");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfUpperCase() {

		NextProtSource.valueOf("GNOMAD");
	}

	@Test
	public void shouldFindValueOfLowerCaseKey() {

		Assert.assertEquals(NextProtSource.GnomAD, NextProtSource.valueOfKey("gnomad"));
	}

	@Test
	public void shouldFindValueOfUpperCaseKey() {

		Assert.assertEquals(NextProtSource.GnomAD, NextProtSource.valueOfKey("GNOMAD"));
	}
}