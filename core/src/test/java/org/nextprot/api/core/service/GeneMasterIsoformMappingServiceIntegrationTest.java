package org.nextprot.api.core.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExonWithSequence;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles({ "dev" })
public class GeneMasterIsoformMappingServiceIntegrationTest extends CoreUnitBaseTest {
        
    @Autowired
	private GeneMasterIsoformMappingService geneMasterIsoformMappingService;

    @Test
    public void NX_ENSG00000178199_hasSomeExons() {
    	String geneName = "NX_ENSG00000178199";
    	List<SimpleExonWithSequence> exonList = geneMasterIsoformMappingService.findGeneExons(geneName);
    	Assert.assertTrue(exonList.size()>0);
    	SimpleExonWithSequence ex = exonList.get(0);
    	Assert.assertEquals("ENSE00001860500", ex.getAccession());
    	Assert.assertEquals("NX_ENSG00000178199", ex.getGeneRegion().getGeneName());
    	Assert.assertEquals(1, ex.getGeneRegion().getFirstPosition());
    	Assert.assertEquals(249, ex.getGeneRegion().getLastPosition());
    	Assert.assertEquals("NX_ENSE00001860500", ex.getName());
    	Assert.assertEquals("CTTAAAAAAAAAAAAACCAAAAAAACCCAAAGCATAACTACTTTTGCAGCTGAACGTGACTGTGGCGTGCAGGAAGTGGAGCATTGGCATGAAGTGGCTCCTAGTGGCTGCTTGGCGCACGCCAGCTGCCCTCCTCTGACTCCAGTGGCACCTGGGGGCCTGGCCTCACTGACGGGAGAACATTGGCGTGAAGGCTGCTGGCGACTGGGCCAGCATTCATTGTGAAGACCGGAGGGACACACCCTGCTG", ex.getSequence());
    }
    
    @Test
    public void NX_P47710_hasSomeGeneRegionsOf_NX_ENSG00000126545() {
    	String entryName = "NX_P47710";
    	List<GeneRegion> regions = geneMasterIsoformMappingService.findEntryGeneRegions(entryName);
    	Assert.assertTrue(regions.size()>0);
    	int prevPos=-1;
    	for (GeneRegion r : regions) {
    		Assert.assertEquals("NX_ENSG00000126545", r.getGeneName());
    		Assert.assertTrue(r.getFirstPosition()>prevPos);
    		prevPos=r.getLastPosition(); // regions should not overlap (except some...)
    	}
    	
    }
    
    
}

