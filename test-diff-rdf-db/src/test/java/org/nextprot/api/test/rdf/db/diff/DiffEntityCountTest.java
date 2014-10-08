package org.nextprot.api.test.rdf.db.diff;

import org.junit.Test;

public class DiffEntityCountTest extends DiffBaseTest {

	@Test
	public void testMasterCount(){
		diffCount("count-valid-master");
	}
	@Test
	public void testIsoformCount(){
		diffCount("count-valid-isoform");
	}
	@Test
	public void testGeneCount(){
		diffCount("count-valid-gene");
	}

	@Test
	public void testMasterPeptideMappingCount(){
		diffCount("count-valid-master-peptide-mapping");
	}
	@Test
	public void testIsoformPeptideMappingCount(){
		diffCount("count-valid-isoform-peptide-mapping");
	}

	@Test
	public void testProteotypicPeptideCount(){
		diffCount("count-proteotypic-peptide");
	}
}
