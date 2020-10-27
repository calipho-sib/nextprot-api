package org.nextprot.api.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;


@ActiveProfiles({"dev", "cache"})
public class TermDictionaryServiceTest extends CoreUnitBaseTest {
	
	@Autowired private TermDictionaryService tdService;
	@Autowired private TerminologyService termService;
	
	@Test
	public void testIt() {
		
		long t0; Map<String,CvTerm> map=null;
		
		List<String> ontologies = new ArrayList<>();
		ontologies.add(TerminologyCv.BgeeDevelopmentalStageCv.toString());
		ontologies.add(TerminologyCv.PsiMiCv.toString());
		ontologies.add(TerminologyCv.EvidenceCodeOntologyCv.toString());
		ontologies.add(TerminologyCv.NextprotAnatomyCv.toString());
		
		System.out.println("Loading BEFORE cache is created");
		for (String onto: ontologies) {
			t0 = System.currentTimeMillis();
			map = tdService.getTermDictionary(onto);
			System.out.println("Time to load  " + onto + " : " + (System.currentTimeMillis()-t0) + " [ms], size=" + map.size());
		}
		
		System.out.println("Loading AFTER  cache is created");
		for (String onto: ontologies) {
			t0 = System.currentTimeMillis();
			map = tdService.getTermDictionary(onto);
			System.out.println("Time to load  " + onto + " : " + (System.currentTimeMillis()-t0) + " [ms], size=" + map.size());
		}
		
		System.out.println("End");
			
	}

	@Test
	public void testItFromTerminologyService() {
		
		List<TerminologyCv> ontologies = new ArrayList<>();
		ontologies.add(TerminologyCv.BgeeDevelopmentalStageCv);
		ontologies.add(TerminologyCv.PsiMiCv);
		ontologies.add(TerminologyCv.EvidenceCodeOntologyCv);
		ontologies.add(TerminologyCv.NextprotAnatomyCv);

		long t0;
		
		for (TerminologyCv onto: ontologies) {

			//System.out.println("Getting list of terms to search in " + onto.toString());
			List<CvTerm> terms = termService.findCvTermsByOntology(onto.toString());
			
			//System.out.println("Doing one fake call just to make sure cache is built and dictionary up in ram");
			CvTerm tx = termService.findCvTermInOntology("schtoumpf", onto);
			assertEquals(tx, null);
	
			//System.out.println("Searching terms using dictionary service...");
			t0 = System.currentTimeMillis();
			for (CvTerm t1: terms) {
				CvTerm t2 = termService.findCvTermInOntology(t1.getAccession(), onto);
				assertEquals(t1.getAccession(), t2.getAccession());
				assertEquals(t1.getId(), t2.getId());
			}
			long ms = System.currentTimeMillis()-t0;
			float f = (float)ms / (float)terms.size() ;
			System.out.println(onto.toString() + ":\ntook " + ms + " ms to find " + terms.size() + " terms by accession in map of size " + terms.size() + ", each search in about " + f + " ms");
		}
		
		System.out.println("End");
	}
	
}
