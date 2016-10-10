package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PepXIntegrationAndValidationTest extends WebIntegrationBaseTest {

    @Autowired
    private PepXService pepXService;
    
	@Autowired
	private DataSourceServiceLocator dsLocator;

	
    @Test
    public void shouldFilterOutPeptidesWhichDontBelongToTheEntry() throws Exception {
		List<Entry> entries = pepXService.findEntriesWithPeptides("TCQAWSSMTPHSHSR,TCQAWS", true);
		for (Entry entry: entries) {
			if (entry.getUniqueName().equals("NX_P08519")) {
				assertTrue(entry.getAnnotations().size()==2);
			} else if (entry.getUniqueName().equals("NX_Q16609")) {
				assertTrue(entry.getAnnotations().size()==1);				
			} else if (entry.getUniqueName().equals("NX_P48544")) {// Check with Alain that this is correct, apparently in UniProt it says: http://www.uniprot.org/uniprot/P48544#sequences TCQARS instead of TCQAWS
				assertTrue(entry.getAnnotations().size()==1);				
			} else {
				assertTrue(false);
			}
		}

    }
    
    @Test
    public void testPepXService() throws Exception {
    	List<String> peptides = getPeptides();        	
    	for(String peptide : peptides){
    		List<Entry> entries = pepXService.findEntriesWithPeptides(peptide, true);
    		for (Entry entry: entries) {
    			System.out.println("testing peptide:" + peptide +  " for " + entry.getUniprotName());
    			// we should have at least one annotation for each entry / peptide match (can be a null variant)
    			assertFalse(entry.getAnnotations().isEmpty());
    		}
    	}
    }

    @Test
    public void testPepXServiceOnPeptideOnVariantWithMultipleDeletionsCausingIndexOutOfRangeError() throws Exception {

    	boolean ok = true;
    	try {
	    	String peptide = "JVPEGPTPDSSEGNJSYJSSJSHJNNJSHJTTSSSF";
	    	pepXService.findEntriesWithPeptides(peptide, true);
    	} catch (Exception e) {
    		ok=false;
    	}
    	assertTrue(ok);
    }

    // deterministic list of peptides
    private List<String> getPeptides() throws Exception {
    	List<String> peptides = Arrays.asList(
    	"RDJAEEJVMYMNNMSSPJTSR",
    	"PDSCCK",
    	"GDFCIQVGR",
    	"SYSACTTDGR",
    	"SCDTPPPCPR",
    	"TCQAWSSMTPHSHSR",
    	"VAYDLVYYVR",
    	"VITVQVANFTLR",
    	"VVTVAALGTNISIHKDEIGK");
    	return peptides;
    }

    

}