package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.dao.impl.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.MainNames;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ConsistencyResourceTest extends AnnotationBuilderIntegrationBaseTest{

	@Autowired private StatementDao statementDao;
	@Autowired private PublicationService publicationService;
	@Autowired private TerminologyService terminologyService;
	@Autowired private MainNamesService mainNamesService;		
	
	@Test
	public void shouldFindAllPublications() {
		
		boolean missingPublications = false;
		List<String> pubmedIds = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(
				StatementField.REFERENCE_ACCESSION, 
				new StatementSimpleWhereClauseQueryDSL(StatementField.REFERENCE_DATABASE, "PubMed"));
		
		//System.out.println("Found " + pubmedIds.size() + " distinct pubmeds");
		for(String p : pubmedIds) {
			if(p != null){ 
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
                // TODO: remove the following line after next release data jan 2018
                if(!"23248292".equals(pubmedId)){
					if(pub == null){
						System.err.println("Can t find publication for " + pubmedId); 
                        missingPublications = true;
					}
				}else {
					System.err.println("FOUND EMPTY PUBLICATION " + pubmedId + ", FIX THIS IN NEXT RELEASE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Probably related to: https://issues.isb-sib.ch/browse/NEXTPROT-1369");
				}
			}
		};
		if(missingPublications)
			Assert.fail();
		
	}

	@Test
	public void shouldFindANameForEveryAllEntriesAndIsoformsAndMemoryUsed() {
		int isoCnt=0;
		int masCnt=0;
		System.gc(); 
		Runtime rt = Runtime.getRuntime();
	    long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
		//System.out.println("Memory used before getting map: " + usedMB);
	    //
		Map<String,MainNames> namesMap = mainNamesService.findIsoformOrEntryMainName();
		//
		System.gc(); 
	    usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
		//System.out.println("Memory used after  getting map: " + usedMB);
		for (String ac: namesMap.keySet()) {
			MainNames n = namesMap.get(ac);
			Assert.assertTrue(n.getName().length()>0);
			if (n.getAccession().contains("-")) isoCnt++; else masCnt++;
		}
		//System.out.println("Mas cnt:" + masCnt);
		//System.out.println("Iso cnt:" + isoCnt);
	}

	@Ignore
	@Test
	public void shouldFindMainNamesMoreThan100TimesFasterAfterFirstTime() {

		long t0 = System.currentTimeMillis();
		Map<String,MainNames> namesMap1 = mainNamesService.findIsoformOrEntryMainName();
		long t1 = System.currentTimeMillis();
		Map<String,MainNames> namesMap2 = mainNamesService.findIsoformOrEntryMainName();
		long t2 = System.currentTimeMillis();
		Map<String,MainNames> namesMap3 = mainNamesService.findIsoformOrEntryMainName();
		long t3 = System.currentTimeMillis();

		//System.out.println("\ncalling mainNamesService 3 times:");
		//System.out.println("call 1 in: " + (t1-t0) + " ms, map size:" + namesMap1.size());
		//System.out.println("call 2 in: " + (t2-t1) + " ms, map size:" + namesMap2.size());
		//System.out.println("call 3 in: " + (t3-t2) + " ms, map size:" + namesMap3.size());
		Assert.assertTrue((t1-t0) > (t2-t1)*100);
		Assert.assertTrue((t1-t0) > (t3-t2)*100);
	}


	@Test
	public void shouldFindAllTerms() {
		
		boolean missingTerms = false;
		List<String> terms = statementDao.findAllDistinctValuesforField(StatementField.ANNOT_CV_TERM_ACCESSION);
		//System.out.println("Found " + terms.size() + " distinct terms");
		for(String t : terms) {
			if(t != null){
				CvTerm term = terminologyService.findCvTermByAccession(t);
				if(term == null){
					System.err.println("Can t find term " + t); 
					missingTerms = true;
				}
			}
		};
		
		if(missingTerms)
			Assert.fail();

	}

}
