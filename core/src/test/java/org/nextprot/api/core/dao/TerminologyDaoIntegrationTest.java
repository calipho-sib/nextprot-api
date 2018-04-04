package org.nextprot.api.core.dao;

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

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@ActiveProfiles({ "dev" })
public class TerminologyDaoIntegrationTest extends CoreUnitBaseTest {
	
	@Autowired TerminologyDao terminologyDao;

	@Test
	public void shouldRetrieveTermXrefs() {
		CvTerm t = terminologyDao.findTerminologyByAccession("DI-03265");
		assertEquals(5, t.getXrefs().size());
		for (DbXref x : t.getXrefs()) {
			// this one is an external xref (no corresponding term loaded in db)
			if (x.getAccession().equals("614224")) {
				assertEquals("http://www.omim.org/entry/614224", x.getResolvedUrl());
			}
			// this one is a related term  (there is actually 1 term corresponding to the AC loaded in db)
			if (x.getAccession().equals("D011666")) {
				assertEquals("http://www.nlm.nih.gov/cgi/mesh/2013/MB_cgi?field=uid&term=D011666", x.getResolvedUrl());
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
