package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;

public class ProteinDigesterBuilderTest {

	@Test(expected = NextProtException.class)
	public void shouldNotDigestProteinWhenNegativeMinPepLength() {

		new ProteinDigesterBuilder().minPepLen(-7);
	}

	@Test(expected = NextProtException.class)
	public void shouldNotDigestProteinWhenNegativeMaxPepLength() {

		new ProteinDigesterBuilder().minPepLen(7).maxPepLen(-77);
	}

	@Test(expected = NextProtException.class)
	public void shouldNotDigestProteinWhenMinPepGreaterThanMaxLength() {

		new ProteinDigesterBuilder().minPepLen(7).maxPepLen(2).build();
	}

	@Test(expected = NextProtException.class)
	public void shouldNotDigestProteinWhenNegativeMCs() {

		new ProteinDigesterBuilder().minPepLen(7).maxPepLen(77).maxMissedCleavageCount(-2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowErrorWhenGettingUnknownProtease() {

		new ProteaseAdapter().getProtease("roudoudou");
	}

	@Test(expected = NextProtException.class)
	public void shouldNotDigestProteinWithUnknownProtease() {

		new ProteinDigesterBuilder().proteaseName("roudoudou");
	}
}