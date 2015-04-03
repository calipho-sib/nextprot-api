package org.nextprot.api.web.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.solr.QueryRequest;

public interface SearchService {

	Set<String> getAccessions(QueryRequest queryRequest);

	List<String> sortAccessions(QueryRequest queryRequest, Set<String> accessions);

}
