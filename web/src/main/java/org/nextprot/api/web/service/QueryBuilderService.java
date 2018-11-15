package org.nextprot.api.web.service;

import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.impl.config.Mode;

public interface QueryBuilderService {

	Query buildQueryForSearch(QueryRequest queryRequest, Entity entity);
	Query buildQueryForProteinLists(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);
	Query buildQueryForSearchIndexes(Entity entity, Mode configuration, QueryRequest request);
	Query buildQueryForAutocomplete(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);
}
