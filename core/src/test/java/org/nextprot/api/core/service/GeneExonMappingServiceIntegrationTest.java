package org.nextprot.api.core.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev" })
public class GeneExonMappingServiceIntegrationTest extends CoreUnitBaseTest {
        
    @Autowired
	private GeneExonMappingService geneExonMappingService;

    @Test
    public void NX_ENSG00000178199_hasSomeExons() {
    	String geneName = "NX_ENSG00000178199";
    	List<SimpleExonWithSequence> exonList = geneExonMappingService.findGeneExons(geneName);
    	Assert.assertTrue(exonList.size()>0);
    	SimpleExonWithSequence ex = exonList.get(0);
    	Assert.assertEquals("ENSE00001860500", ex.getAccession());
    	Assert.assertEquals(1, ex.getGeneRegion().getFirstPosition());
    	Assert.assertEquals(249, ex.getGeneRegion().getLastPosition());
    	Assert.assertEquals("NX_ENSE00001860500", ex.getName());
    	Assert.assertEquals("CTTAAAAAAAAAAAAACCAAAAAAACCCAAAGCATAACTACTTTTGCAGCTGAACGTGACTGTGGCGTGCAGGAAGTGGAGCATTGGCATGAAGTGGCTCCTAGTGGCTGCTTGGCGCACGCCAGCTGCCCTCCTCTGACTCCAGTGGCACCTGGGGGCCTGGCCTCACTGACGGGAGAACATTGGCGTGAAGGCTGCTGGCGACTGGGCCAGCATTCATTGTGAAGACCGGAGGGACACACCCTGCTG", ex.getSequence());
    	
    }
    
    
    
}

