package org.nextprot.api.web.service;

import org.nextprot.api.solr.dto.QueryRequest;

import java.util.List;
import java.util.Set;

public interface SearchService {

	Set<String> getAccessions(QueryRequest queryRequest);

	List<String> sortAccessions(QueryRequest queryRequest, Set<String> accessions);

}
