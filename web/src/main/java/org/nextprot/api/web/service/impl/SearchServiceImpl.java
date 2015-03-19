package org.nextprot.api.web.service.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SearchResult.SearchResultFacet;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.web.service.QueryBuilderService;
import org.nextprot.api.web.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SolrService solrService;

	@Autowired
	private QueryBuilderService queryBuilderService;

	@Override
	public Set<String> getAssessions(QueryRequest queryRequest) {
		Set<String> set = new LinkedHashSet<String>();
		try {
			Query query = this.queryBuilderService.buildQueryForSearchIndexes("entry", "simple", queryRequest);
			SearchResult results = solrService.executeIdQuery(query);
			Map<String, SearchResultFacet> facets = results.getFacets();
			SearchResultFacet srf = facets.get("id");
			for (Pair<String, Long> f : srf.getFacetFields()) {
				set.add(f.getFirst());
			}

		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when retrieving accessions");
		}
		return set;
	}

}
