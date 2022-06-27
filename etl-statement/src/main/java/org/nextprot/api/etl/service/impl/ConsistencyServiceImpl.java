package org.nextprot.api.etl.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.ANNOT_CV_TERM_ACCESSION;
import static org.nextprot.commons.statements.specs.CoreStatementField.REFERENCE_ACCESSION;
import static org.nextprot.commons.statements.specs.CoreStatementField.REFERENCE_DATABASE;

@Service
public class ConsistencyServiceImpl implements ConsistencyService{
	
	private static final Log LOGGER = LogFactory.getLog(ConsistencyServiceImpl.class);
	
	private static final Set<String> EXTRA_FIELD_TERM_NAMES = new HashSet<>(Arrays.asList("DISEASE_ACC", "TISSUE_ACC", "CELL_LINE_ACC"));
	
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
		
		// Get term values stored in ANNOT_CV_TERM_ACCESSION
		Set<String> terms = new HashSet<>(statementDao.findAllDistinctValuesforField(ANNOT_CV_TERM_ACCESSION));
		
		// Get term values stored in EXTRA_FIELDS
		for (String termFieldName: EXTRA_FIELD_TERM_NAMES) {
			List<String> extraFields = statementDao.findDistinctExtraFieldsTerms(termFieldName);
			terms.addAll(extraFields.stream()
									.filter(StringUtils::isNotBlank)
									.map(extraValue -> {
										Map<String, String> extraMap = org.nextprot.commons.utils.StringUtils.deserializeAsMapOrNull(extraValue);
										String termAcc = extraMap == null? null: extraMap.get(termFieldName);
										if (StringUtils.isNotBlank(termAcc)) {
											return termAcc;
										}
										return null;
									})
									.collect(Collectors.toSet()));
		}
		
		// an ANNOT_CV_TERM_ACCESSION or an EXTRA_FIELD can be null
		terms.remove(null);
		
		Set<String> cvTerms = terminologyService.findCvTermsByAccessions(terms).stream()
												.map(CvTerm::getAccession)
												.collect(Collectors.toSet());
		terms.removeAll(cvTerms);
		
		if (terms.size() > 0) {
			LOGGER.error("Missing terms: " + terms);
		}
		
		return new ArrayList<>(terms);
	}
}
