package org.nextprot.api.etl;

import org.junit.Assert;
import org.junit.Test;

public class StatementSourceEnumTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfLowerCase() {

		StatementSourceEnum.valueOf("gnomad");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfUpperCase() {

		StatementSourceEnum.valueOf("GNOMAD");
	}

	@Test
	public void shouldFindValueOfLowerCaseKey() {

		Assert.assertEquals(StatementSourceEnum.GnomAD, StatementSourceEnum.valueOfKey("gnomad"));
	}

	@Test
	public void shouldFindValueOfUpperCaseKey() {

		Assert.assertEquals(StatementSourceEnum.GnomAD, StatementSourceEnum.valueOfKey("GNOMAD"));
	}
}