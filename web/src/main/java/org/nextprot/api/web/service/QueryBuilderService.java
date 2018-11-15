package org.nextprot.api.web.service;

import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.dto.QueryRequest;

public interface QueryBuilderService {

	Query buildQueryForSearch(QueryRequest queryRequest, Entity entity);
	Query buildQueryForProteinLists(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);
	Query buildQueryForSearchIndexes(Entity entity, SearchMode configuration, QueryRequest request);
	Query buildQueryForAutocomplete(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);
}
