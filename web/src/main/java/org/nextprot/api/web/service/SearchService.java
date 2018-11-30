package org.nextprot.api.web.service;

import org.nextprot.api.solr.query.dto.QueryRequest;

import java.util.List;
import java.util.Set;

public interface SearchService {

	Set<String> findAccessions(QueryRequest queryRequest);

	/** sort accession by solr given a SortConfig.Criteria (by AC, CHROMOSOME, FAMILY, GENE,  LENGTH,  NAME, PROTEIN or SCORE) */
	List<String> sortAccessionsWithSolr(QueryRequest queryRequest, Set<String> accessions);
}
