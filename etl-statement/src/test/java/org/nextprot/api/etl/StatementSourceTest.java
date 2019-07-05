package org.nextprot.api.etl;

import org.junit.Assert;
import org.junit.Test;

public class StatementSourceTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfLowerCase() {

		StatementSource.valueOf("gnomad");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindValueOfUpperCase() {

		StatementSource.valueOf("GNOMAD");
	}

	@Test
	public void shouldFindValueOfLowerCaseKey() {

		Assert.assertEquals(StatementSource.GnomAD, StatementSource.valueOfKey("gnomad"));
	}

	@Test
	public void shouldFindValueOfUpperCaseKey() {

		Assert.assertEquals(StatementSource.GnomAD, StatementSource.valueOfKey("GNOMAD"));
	}
}