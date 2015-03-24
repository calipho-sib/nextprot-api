package org.nextprot.api.web.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SearchResult.SearchResultFacet;
import org.nextprot.api.solr.SearchResult.SearchResultItem;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.web.service.QueryBuilderService;
import org.nextprot.api.web.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

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
	
	
	@Override
	public List<String> getAccessionsFilteredAndSorted(QueryRequest queryRequest, Set<String> accessions) {
		

		List<String> set = new ArrayList<String>();
		try {
			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			queryRequest.setRows("25000");
			Query query =  queryBuilderService.buildQueryForSearchIndexes("entry", "pl_search", queryRequest);
			SearchResult result = this.solrService.executeIdQuery(query);
			List<SearchResultItem> items = result.getResults();
			for (SearchResultItem i : items) {
				set.add((String) i.getProperties().get("id"));
			}

		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when search for pl search");
		}
		return set;
	}

}
