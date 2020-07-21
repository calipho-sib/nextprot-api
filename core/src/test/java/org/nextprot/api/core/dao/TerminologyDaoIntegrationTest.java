package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@ActiveProfiles({ "dev" })
public class TerminologyDaoIntegrationTest extends CoreUnitBaseTest {
	
	@Autowired TerminologyDao terminologyDao;

	@Test
	public void shouldDealWithAbbreviations() {
		CvTerm t = terminologyDao.findTerminologyByAccession("DI-00002");
		assertEquals(true, t.getProperties()!=null);
		assertEquals(true, 
			t.getProperties().stream().anyMatch(p -> p.getPropertyName().equals("abbreviation") && p.getPropertyValue().equals("HADH deficiency"))
		);
		assertEquals(true, t.getSynonyms().size()==3); // "HADH deficiency" is now an abbreviation
	}


	@Test
	public void shouldDealWithSynonymsForEnzymes() {
		//Avenacosidase should be discarded
		CvTerm t = terminologyDao.findTerminologyByAccession("3.2.1.188");
		Assert.assertTrue(t.getSynonyms().isEmpty());
	}

	@Test
	public void shouldDealWithSynonymsForNextprotDomainsDNABINDING() {
		//Should remove ETS DNA-binding domain
		CvTerm t = terminologyDao.findTerminologyByAccession("DO-00210");
		Assert.assertTrue(!t.getSynonyms().contains("ETS"));
	}

	@Test
	public void shouldDealWithSynonymsForNextprotDomainsDOMAIN() {
		//Should remove ETS DNA-binding domain
		CvTerm t = terminologyDao.findTerminologyByAccession("DO-00348");
		Assert.assertTrue(!t.getSynonyms().contains("KA1"));
	}


	@Test
	public void shouldRetrieveTermXrefs() {
		CvTerm t = terminologyDao.findTerminologyByAccession("DI-03265");
		assertEquals(5, t.getXrefs().size());
		for (DbXref x : t.getXrefs()) {
			// this one is an external xref (no corresponding term loaded in db)
			if (x.getAccession().equals("614224")) {
				assertEquals("https://www.omim.org/entry/614224", x.getResolvedUrl());
			}
			// this one is a related term  (there is actually 1 term corresponding to the AC loaded in db)
			if (x.getAccession().equals("D011666")) {
				assertEquals("https://meshb.nlm.nih.gov/record/ui?ui=D011666", x.getResolvedUrl());
				assertEquals("Pulmonary Valve Stenosis", x.getPropertyValue("term_name"));
				assertEquals("MeSH", x.getPropertyValue("term_ontology_display_name"));
			}
		}
		
		t = terminologyDao.findTerminologyByAccession("D000783");
		assertEquals(1, t.getXrefs().size());
		for (DbXref x : t.getXrefs()) {
			// this one is a related term  (there is actually 1 term corresponding to the AC loaded in db)
			if (x.getAccession().equals("DI-03265")) {
				assertEquals("Retinal arterial macroaneurysm with supravalvular pulmonic stenosis", x.getPropertyValue("term_name"));
				assertEquals("UniProtKB disease", x.getPropertyValue("term_ontology_display_name"));
			}
		}
	}

	@Test
	public void shouldRetrieveTermWithSelfXref() {
		// terms having an external definition should have a self xref
		// Examples: GO:0072576, MeSH D017093
		String  ac; CvTerm t; DbXref x;

		ac = "GO:0072576";
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("GO", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("https://www.ebi.ac.uk/QuickGO/term/GO:0072576", x.getResolvedUrl());
		
		ac = "D017093";
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("MeSH", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("https://meshb.nlm.nih.gov/record/ui?ui=D017093", x.getResolvedUrl());
		
		
	}

	@Test
	public void shouldRetrieveTermWithSelfXrefHavingNoResolvedUrl() {
		// terms having a xref from a db with NO link_url defined i.e. some UniProt and neXtProt xrefs
		// Examples: neXtProt tissue (CALOHA) TS-0252, UniProt PTMs PTM-004
		String ac; CvTerm t; DbXref x;

		ac = "TS-0252";  
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("UniProt control vocabulary", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("None", x.getResolvedUrl());
		
		ac = "DI-02634"; // this one resolves now
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("UniProt control vocabulary", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("http://www.uniprot.org/diseases/DI-02634", x.getResolvedUrl());

		ac = "CVTO_0008";
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("neXtProt control vocabulary", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("None", x.getResolvedUrl());

		ac = "CVOR_0002";
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("neXtProt control vocabulary", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("None", x.getResolvedUrl());

		ac = "CVAN_0016";
		t = terminologyDao.findTerminologyByAccession(ac);
		x = t.getSelfXref();
		assertEquals(false, x==null);
		assertEquals(false, x.getDbXrefId()==null);
		assertEquals(ac, x.getAccession());
		assertEquals("neXtProt control vocabulary", x.getDatabaseName());
		assertEquals("Ontologies", x.getDatabaseCategory());
		assertEquals("None", x.getResolvedUrl());

	}

	
	
		
	@Test
	public void shouldTheTerminologiesByInSyncWithDB() {
		
		List<String> terminologies = terminologyDao.findTerminologyNamesList();
		
		List<TerminologyCv> tCv = 
				Arrays.stream(TerminologyCv.values())
				.collect(Collectors.toList());

		assertEquals(terminologies.size(), tCv.size());
		
		for(TerminologyCv t : tCv){
			if (! t.equals(TerminologyCv.NextprotCellosaurusCv)) { // TEMP pass thru
				if(!terminologies.contains(StringUtils.camelToKebabCase(t.name()))){
					fail(t + " is not contained anymore");
				}
			}
		}
		
	}

	
	
}
