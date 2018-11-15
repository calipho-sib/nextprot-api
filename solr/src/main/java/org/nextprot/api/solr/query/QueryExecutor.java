package org.nextprot.api.solr.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.query.dto.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class QueryExecutor {

	private static final Log LOGGER = LogFactory.getLog(QueryExecutor.class);

	private final SolrCore core;

	public QueryExecutor(SolrCore core) {

		this.core = core;
	}

	public SearchResult execute(Query query) throws SolrServerException, QueryConfiguration.BuildSolrQueryException {

		SolrQuery solrQuery = core.getDefaultConfig().convertQuery(query);

		return execute(solrQuery);
	}

	public SearchResult execute(SolrQuery solrQuery) throws SolrServerException {

		logSolrQuery(solrQuery);
		QueryResponse response = core.newSolrClient().query(solrQuery, SolrRequest.METHOD.POST);

		SearchResult results = new SearchResult(core.getAlias().getName(), core.getName());

		SolrDocumentList docs = response.getResults();
		LOGGER.debug("Response doc size:" + docs.size());
		List<Map<String, Object>> res = new ArrayList<>();

		Map<String, Object> item;
		for (SolrDocument doc : docs) {

			item = new HashMap<>();

			for (Map.Entry<String, Object> e : doc.entrySet())
				item.put(e.getKey(), e.getValue());

			res.add(item);
		}

		results.addAllResults(res);
		if (solrQuery.getStart() != null)
			results.setStart(solrQuery.getStart());

		results.setRows(solrQuery.getRows());
		results.setElapsedTime(response.getElapsedTime());
		results.setFound(docs.getNumFound());

		if (docs.getMaxScore() != null)
			results.setScore(docs.getMaxScore());

		// Facets
		List<FacetField> facetFields = response.getFacetFields();
		LOGGER.debug("Response facet fields:" + facetFields.size());
		if (!facetFields.isEmpty()) {

			SearchResult.Facet facet;

			for (FacetField ff : facetFields) {
				facet = new SearchResult.Facet(ff.getName());
				LOGGER.debug("Response facet field:" + ff.getName() + " count:" + ff.getValueCount());

				for (FacetField.Count c : ff.getValues())
					facet.addFacetField(c.getName(), c.getCount());
				results.addSearchResultFacet(facet);
			}
		}

		// Spellcheck
		SpellCheckResponse spellcheckResponse = response.getSpellCheckResponse();

		if (spellcheckResponse != null) {
			SearchResult.Spellcheck spellcheckResult = new SearchResult.Spellcheck();

			List<SpellCheckResponse.Suggestion> suggestions = spellcheckResponse.getSuggestions();
			List<SpellCheckResponse.Collation> collations = spellcheckResponse.getCollatedResults();

			if (collations != null) {
				for (SpellCheckResponse.Collation c : collations)
					spellcheckResult.addCollation(c.getCollationQueryString(), c.getNumberOfHits());
			}

			if (suggestions != null)
				for (SpellCheckResponse.Suggestion s : suggestions)
					spellcheckResult.addSuggestions(s.getToken(), s.getAlternatives());

			results.setSpellCheck(spellcheckResult);
		}

		return results;
	}

	private void logSolrQuery(SolrQuery sq) {
		Set<String> params = new TreeSet<>();
		for (String p : sq.getParameterNames()) params.add(p + " : " + sq.get(p));
		LOGGER.debug("SolrQuery ============================================================== in executeSolrQuery");
		for (String p : params) {
			LOGGER.debug("SolrQuery " + p);
		}
	}

	public List<String> executeAndGetAccessions(SolrQuery query) throws SolrServerException {

		List<String> accessions = new ArrayList<>();

		SearchResult result = execute(query);
		for (Map<String, Object> item : result.getResults()) {
			accessions.add((String) item.get("id"));
		}

		return accessions;
	}
}
