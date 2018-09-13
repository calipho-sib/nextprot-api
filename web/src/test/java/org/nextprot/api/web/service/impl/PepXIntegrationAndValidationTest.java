package org.nextprot.api.web.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.SequenceUnicity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.PepXService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    
    @Test
    public void testSinglePeptideWithUnicityUnique() throws Exception {

    	// NX_PEPT01668698	DJCQAQGVAJQTMK
    	
    	String peptide = "DICQAQGVAIQTMK"; // replaced any J in original with L otherwise pepx don't match the peptide !!!!
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	Assert.assertEquals(1, result.size());
    	List<Annotation> annots = result.get(0).getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION);
    	Assert.assertEquals(1, annots.size());
    	Annotation a = annots.get(0);
    	Assert.assertEquals("DICQAQGVAIQTMK", a.getCvTermName());
    	assertTrue(a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).stream().allMatch(p -> p.getValue().equals("Y")));
    	assertTrue(a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.UNIQUE.name())));
    	assertTrue(a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.UNIQUE.name())));
    	
    }    
    
    @Test
    public void testSinglePeptideWithUnicityPseudoUnique() throws Exception {

        // NX_PEPT01180319 AAQEJQEGQR is pseudo unique
    	// equivalent isoforms are "NX_P35520-1","NX_P0DN79-1"
    	
    	String peptide = "AAQEIQEGQR"; // replaced any J in original with L otherwise pepx don't match the peptide !!!!
    	Set<String> equivIsoSet = new TreeSet<String>(Arrays.asList("NX_P35520-1","NX_P0DN79-1" ));
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	assertTrue( result.size() > 1); // multiple entries but they're sharing an isoform having the same sequence
    	assertTrue( result.stream()
    		.flatMap(entry -> entry.getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION).stream())
    		.allMatch(a -> 
    			a.getCvTermName().equals(peptide)  && 
    	    	a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).stream().allMatch(p -> p.getValue().equals("Y")) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.PSEUDO_UNIQUE.name())) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.PSEUDO_UNIQUE.name())) &&
    			new TreeSet<String>(a.getSynonyms()).equals(equivIsoSet))
    	);
    }    

    
    @Test
    public void testSinglePeptideWithUnicityNonUnique() throws Exception {

    	// NX_PEPT00000100 AFVHWYVGEGMEEGEFSEAR
    	
    	String peptide = "AFVHWYVGEGMEEGEFSEAR"; 
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	assertTrue( result.size() > 1); // multiple entries 
    	assertTrue(result.stream()
    		.flatMap(entry -> entry.getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION).stream())
    		.allMatch(a -> a.getCvTermName().equals(peptide) &&
        	    a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).stream().allMatch(p -> p.getValue().equals("N")) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE.name())) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE.name()))
    		)
    	);
    	
    }    

    @Test
    public void testSinglePeptideWithUnicityStatusDifferentWithVariants() throws Exception {

    	// LSAASGYSDVTDS 
    	// => pepx matches are unique with no variant:    Q13670-1
    	// => pepx matches are non unique with variants:  Q13670-1, Q86UW9-1-419, Q86UW9-2-372
    	
    	String peptide = "LSAASGYSDVTDS"; 
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	assertTrue( result.size() > 1); // multiple entries 
    	assertTrue(result.stream()
    		.flatMap(entry -> entry.getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION).stream())
    		.allMatch(a -> a.getCvTermName().equals(peptide) &&
        	    a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).stream().allMatch(p -> p.getValue().equals("Y")) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.UNIQUE.name())) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE.name()))
    		)
    	);
    	
    }    


    @Test
    public void testSinglePeptideWithUnicityStatusNonUniqueButIncludingEquivalentIsoforms() throws Exception {

    	// AIPPSQLDSQIDDFTGFSK 
    	// => pepx matches P0DMU7-1,P0DMU8-1,P0DMU9-1,P0DMV0-1,P0DMV1-1,P0DMV2-1,Q5DJT8-1,Q5HYN5-1,Q8NHU0-1
    	// and among these above the following are known as equivalent: NX_P0DMV1-1, NX_P0DMV2-1, NX_Q5DJT8-1
    	
    	String peptide = "AIPPSQLDSQIDDFTGFSK"; 
    	Set<String> equivIsoSet = new TreeSet<String>(Arrays.asList("NX_P0DMV1-1", "NX_P0DMV2-1", "NX_Q5DJT8-1" ));
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	assertTrue( result.size() > 1); // multiple entries 
    	assertTrue(result.stream()
    		.flatMap(entry -> entry.getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION).stream())
    		.allMatch(a -> a.getCvTermName().equals(peptide) &&
       	    	a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).stream().allMatch(p -> p.getValue().equals("N")) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE.name())) &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.NOT_UNIQUE.name())) &&
    			new TreeSet<String>(a.getSynonyms()).equals(equivIsoSet)
    		)
    	);
    	
    }    
   
    @Test
    public void testSinglePeptideMatchingOnlyVariantIsoform() throws Exception {

    	// YPVVKRTEGPAGHSKGELAP 
    	
    	String peptide = "YPVVKRTEGPAGHSKGELAP"; 
    	Set<String> equivIsoSet = new TreeSet<String>();
    	List<Entry> result = pepXService.findEntriesWithPeptides(peptide, true);
    	assertTrue( result.size() == 1); // single entry 
    	assertTrue(result.stream()
    		.flatMap(entry -> entry.getAnnotationsByCategory(AnnotationCategory.PEPX_VIRTUAL_ANNOTATION).stream())
    		.allMatch(a -> a.getCvTermName().equals(peptide) &&
       	    	a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_PROTEOTYPICITY).size()==0 &&
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY).size()==0 && 
    			a.getPropertiesByKey(PropertyApiModel.NAME_PEPTIDE_UNICITY_WITH_VARIANTS).stream().allMatch(p -> p.getValue().equals(SequenceUnicity.Value.UNIQUE.name())) &&
    			a.getSynonyms()==null
    		)
    	);
    	
    }    
   
    
}