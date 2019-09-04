package org.nextprot.api.commons.bio.variation.prot.digestion;

import java.util.List;

import org.biojava.bio.proteomics.Protease;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;

public class ProteinDigesterBuilderTest {

	
	@Test
	public void shouldReturnPeptidesWithProducedWithMisceleavageCount() {
		
/*		digestion shoul return these peptides with 3 of which have been produced with miscleavage = 1
 		YOUR - 0
		YOURDEADK - 1
		DEADK - 0
		DEADKOR - 1
		OR - 0
		ORALIVE - 1
		ALIVE - 0
*/
		ProteinDigester digester = new ProteinDigesterBuilder()
				.minPepLen(1)
				.proteaseName(Protease.TRYPSIN)
				.maxMissedCleavageCount(1)
				.build();
		Protein protein = new Protein("Prot1","YOURDEADKORALIVE");
		List<Peptide> peptides = digester.digest(protein);
		int mcCount=0;
		for (Peptide p: peptides) mcCount += p.getProducedWithMisceleavageCount();
		Assert.assertTrue(mcCount==3);
		
	}
	
	
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