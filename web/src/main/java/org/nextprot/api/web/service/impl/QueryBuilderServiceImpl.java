package org.nextprot.api.web.service.impl;

import com.google.common.base.Joiner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.dto.Query;
import org.nextprot.api.solr.dto.QueryRequest;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Service that builds a nextprot query from a query request (http request)
 * 
 * @author dteixeira
 *
 */
@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private SolrService queryService;
	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Autowired
	private UserQueryService userQueryService;
	@Autowired
	private UserProteinListService proteinListService;

	private static final Log Logger = LogFactory.getLog(QueryBuilderServiceImpl.class);

	@Override
	public Query buildQueryForSearch(QueryRequest queryRequest, String indexName) {

		Logger.debug(queryRequest.toPrettyString());

		if (queryRequest.isEntryAccessionSetDefined()) {
            Logger.debug("queryRequest.hasEntryAccessionList()");

            return buildQueryForSearchIndexes(indexName, queryRequest, buildQueryStringFromEntryAccessions(queryRequest.getEntryAccessionSet()));
        }
        else if (queryRequest.hasList()) {
			Logger.debug("queryRequest.hasList()");
			UserProteinList proteinList;
			if(StringUtils.isWholeNumber(queryRequest.getListId())){ //Private id is used
				proteinList = this.proteinListService.getUserProteinListById(Long.valueOf(queryRequest.getListId()));
			} else { //public id is used
				proteinList = this.proteinListService.getUserProteinListByPublicId(queryRequest.getListId());
			}
            return buildQueryForSearchIndexes(indexName, queryRequest, buildQueryStringFromEntryAccessions(proteinList.getAccessionNumbers()));
		}
		else if (queryRequest.hasNextProtQuery()) {
			Logger.debug("queryRequest.hasNextProtQuery()");
			UserQuery uq;

			//TODO  i don t think this is used anymore, checkout the logs
			if(StringUtils.isWholeNumber(queryRequest.getQueryId())){ //Private id is used
				Logger.fatal("Yes I am beeing used!!!");
				uq = userQueryService.getUserQueryById(Long.valueOf(queryRequest.getQueryId()));
			}else { //public id is used
				uq = userQueryService.getUserQueryByPublicId(queryRequest.getQueryId());
			}

            return buildQueryForSearchIndexes(indexName, queryRequest, buildQueryStringFromEntryAccessions(
                    new HashSet<>(sparqlService.findEntries(uq.getSparql(),
                            sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()))));

        } else if (queryRequest.hasSparql()) {
			Logger.debug("queryRequest.hasSparql()");

            return buildQueryForSearchIndexes(indexName, queryRequest, buildQueryStringFromEntryAccessions(
                    new HashSet<>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()))));

		} else {
			Logger.debug("queryRequest.default for simple search");

			if (queryRequest.getQuery() == null) {
			    queryRequest.setQuery("");
            }

			return queryService.buildQueryForSearchIndexes(indexName, "simple", queryRequest);
		}

	}

	@Override
	public Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return queryService.buildQueryForProteinLists(indexName, queryString, quality, sort, order, start, rows, filter);
	}

	@Override
	public Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request) {
		return queryService.buildQueryForSearchIndexes(indexName, configurationName, request);
	}

	@Override
	public Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return queryService.buildQueryForAutocomplete(indexName, queryString, quality, sort, order, start, rows, filter);
	}

    private String buildQueryStringFromEntryAccessions(Set<String> accessions) {

        // In case there is no result
        if (accessions.isEmpty()) {
            // There is no entry with NULL value, so the result will be
            // empty, but the result structure will be maintained (could be
            // replace by SearchResult factory where you create an empty
            // result)
            accessions.add("NULL");
        }

        return "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
    }

    private Query buildQueryForSearchIndexes(String indexName, QueryRequest queryRequest, String queryString) {

	    queryRequest.setQuery(queryString);

        return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);
    }
}
