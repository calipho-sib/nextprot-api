package org.nextprot.api.etl.service.impl;

import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.core.dao.impl.StatementSimpleWhereClauseQueryDSL;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.etl.service.ConsistencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.commons.statements.NXFlatTableStatementField.*;

@Service
public class ConsistencyServiceImpl implements ConsistencyService{

	@Autowired PublicationService publicationService;
	@Autowired TerminologyService terminologyService;
	@Autowired StatementDao statementDao;
	
	@Override
	public List<String> findMissingPublications() {
		
		List<String> missingPublications = new ArrayList<>();

		Arrays.asList(XrefDatabase.PUB_MED, XrefDatabase.DOI).forEach(referenceDB -> {
		
			List<String> ids = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(
					REFERENCE_ACCESSION,
					new StatementSimpleWhereClauseQueryDSL(REFERENCE_DATABASE, referenceDB.getName()));
			
			for(String id : ids) {
				if(id != null){ 
					Publication pub = publicationService.findPublicationByDatabaseAndAccession(referenceDB.getName(), id);
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
		
		List<String> terms = statementDao.findAllDistinctValuesforField(ANNOT_CV_TERM_ACCESSION);
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
