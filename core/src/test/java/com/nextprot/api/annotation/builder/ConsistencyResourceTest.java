package com.nextprot.api.annotation.builder;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;

public class ConsistencyResourceTest extends AnnotationBuilderIntegrationBaseTest{

	@Autowired private StatementDao statementDao;
	@Autowired private PublicationService publicationService;
	
	@Test
	public void shouldFindAllPublications() {
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforField(StatementField.REFERENCE_PUBMED);
		System.out.println("Found " + pubmedIds.size() + " distinct pubmeds");
		pubmedIds.forEach(p -> {
			if(p != null){ 
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				List<Publication> pubs = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if(pubs.size() != 1){
					System.err.println("Can t find publication for " + pubmedId); 
				}
			}
		});

	}

	
	@Test
	public void shouldFindAllBiologicalAccession() {
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforField(StatementField.REFERENCE_PUBMED);
		System.out.println("Found " + pubmedIds.size() + " distinct pubmeds");
		pubmedIds.forEach(p -> {
			if(p != null){
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				List<Publication> pubs = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if(pubs.size() != 1){
					System.err.println("Can t find publication for " + pubmedId); 
				}
			}
		});

	}

	
	
}
