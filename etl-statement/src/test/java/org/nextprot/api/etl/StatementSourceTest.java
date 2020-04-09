package org.nextprot.api.etl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.specs.StatementField;

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
	public void shouldContainExtrField() {

		StatementSource src = StatementSource.ENYO;
		/*
		for (StatementField f: src.getFields()) {
			System.out.println("StatementField " + f.getClass() + " : " + f.getName());
		}
		for (StatementField f: src.getSpecifications().getFields()) {
			System.out.println("StatementField " + f.getClass() + " : " + f.getName());
		}
		*/
		Assert.assertTrue(src.getSpecifications().hasField("PSIMI_ID"));
		Assert.assertTrue(src.getSpecifications().getField("PSIMI_ID").getName().equals("PSIMI_ID"));
	
		Assert.assertTrue(src.getField("PSIMI_ID").getName().equals("PSIMI_ID"));
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