package org.nextprot.api.etl.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.etl.service.ConsistencyService;
import org.nextprot.commons.statements.StatementField;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;

public class ConsistencyServiceImpl implements ConsistencyService{

	@Autowired PublicationService publicationService;
	@Autowired TerminologyService terminologyService;
	@Autowired StatementDao statementDao;
	
	@Override
	public List<String> findMissingPublications() {
		
		List<String> missingPublications = new ArrayList<>();
		
		List<String> pubmedIds = statementDao.findAllDistinctValuesforFieldWhereFieldEqualsValues(StatementField.REFERENCE_ACCESSION, StatementField.REFERENCE_DATABASE, "PubMed");
		for(String p : pubmedIds) {
			if(p != null){ 
				String pubmedId = p.replace("(PubMed,", "").replace(")", "");
				Publication pub = publicationService.findPublicationByDatabaseAndAccession("PubMed", pubmedId);
				if(pub == null){
					missingPublications.add(p);
				}
			}
		};
		
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
