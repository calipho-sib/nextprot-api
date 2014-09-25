package org.nextprot.api.web.misc.to.be.organized;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
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
