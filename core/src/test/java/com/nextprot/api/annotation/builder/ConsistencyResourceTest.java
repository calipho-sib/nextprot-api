package com.nextprot.api.annotation.builder;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.junit.Test;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ConsistencyResourceTest extends AnnotationBuilderIntegrationBaseTest{

	@Autowired private StatementDao statementDao;
	@Autowired private PublicationService publicationService;
	@Autowired private TerminologyService terminologyService;
	
	@Test
	public void shouldFindAllPublications() {
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforField(StatementField.REFERENCE_PUBMED);
		System.out.println("Found " + pubmedIds.size() + " distinct pubmeds");
		pubmedIds.forEach(p -> {
			if(p != null){ 
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if(pub == null){
					System.err.println("Can t find publication for " + pubmedId); 
				}
			}
		});

	}

	
	@Test
	public void shouldFindAllPubmeds() {
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforField(StatementField.REFERENCE_PUBMED);
		System.out.println("Found " + pubmedIds.size() + " distinct pubmeds");
		pubmedIds.forEach(p -> {
			if(p != null){
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if(pub == null) {
					System.err.println("Can t find publication for " + pubmedId); 
				}
			}
		});

	}

	

	@Test
	public void shouldFindAllTerms() {
		
		List<String> terms = statementDao.findAllDistinctValuesforField(StatementField.ANNOT_CV_TERM_ACCESSION);
		System.out.println("Found " + terms.size() + " distinct terms");
		terms.forEach(t -> {
			if(t != null){
				CvTerm term = terminologyService.findCvTermByAccession(t);
				if(term == null){
					System.err.println("Can t find term " + t); 
				}
			}
		});

	}

}
