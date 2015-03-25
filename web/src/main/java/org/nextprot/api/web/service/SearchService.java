package org.nextprot.api.web.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.solr.QueryRequest;

public interface SearchService {

	public Set<String> getAssessions(QueryRequest queryRequest);

	List<String> getAccessionsFilteredAndSorted(QueryRequest queryRequest, Set<String> accessions, Integer limit);

}
	
