package org.nextprot.api.etl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.etl.service.ConsistencyService;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.nextprot.api.core.dao.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.api.core.dao.StatementDao;

@Service
public class ConsistencyServiceImpl implements ConsistencyService{

	@Autowired PublicationService publicationService;
	@Autowired TerminologyService terminologyService;
	@Autowired StatementDao statementDao;
	
	@Override
	public List<String> findMissingPublications() {
		
		List<String> missingPublications = new ArrayList<>();

		Arrays.asList("PubMed", "DOI").stream().forEach(referenceDB -> {
		
			List<String> ids = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(
					StatementField.REFERENCE_ACCESSION, 
					new StatementSimpleWhereClauseQueryDSL(StatementField.REFERENCE_DATABASE, referenceDB));
			
			for(String id : ids) {
				if(id != null){ 
					Publication pub = publicationService.findPublicationByDatabaseAndAccession(referenceDB, id);
					if(pub == null){
						missingPublications.add(referenceDB + id);
					}
				}
			};

		});

		return missingPublications;
	}

	@Override
	public List<String> findMissingCvTerms() {
		

		List<String> missingCvTerms = new ArrayList<>();
		
		List<String> terms = statementDao.findAllDistinctValuesforField(StatementField.ANNOT_CV_TERM_ACCESSION);
		for(String t : terms) {
			if(t != null){
				CvTerm term = terminologyService.findCvTermByAccession(t);
				if(term == null){
					missingCvTerms.add(t);
				}
			}
		}
		
		return missingCvTerms;

	}


}
