package org.nextprot.api.web.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SearchResult.SearchResultFacet;
import org.nextprot.api.solr.SearchResult.SearchResultItem;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
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

	private final Log Logger = LogFactory.getLog(SearchServiceImpl.class);
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
	@Autowired
	private SolrConfiguration configuration;
	@Autowired
	private QueryBuilderService queryBuilderService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;




	@Override
	public Set<String> getAccessions(QueryRequest queryRequest) {
		if (queryRequest.hasAccs()) {
			
			Logger.debug("queryRequest.hasAccs()");
			return new HashSet<String>(queryRequest.getAccs());

		} else if (queryRequest.hasChromosome()) {
			
			Logger.debug("queryRequest.hasChromosome()");
			return new HashSet<String>(this.masterIdentifierService.findUniqueNamesOfChromosome(queryRequest.getChromosome()));

		} else if (queryRequest.hasList()) {
			
			Logger.debug("queryRequest.hasList()");
			UserProteinList proteinList = this.proteinListService.getUserProteinListByPublicId(queryRequest.getListId());
			return proteinList.getAccessionNumbers();

		} else if (queryRequest.hasNextProtQuery()) {
		
			UserQuery uq  = userQueryService.getUserQueryByPublicId(queryRequest.getQueryId());
			return new HashSet<String>(sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
		
		} else if (queryRequest.hasSparql()) {
		
			return new HashSet<String>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
		
		} else {

			String originalQuality = queryRequest.getQuality();
			//Set gold quality if not specified
			if((queryRequest.getQuality() == null) || (queryRequest.getQuality().equals(""))){
				queryRequest.setQuality("gold");
			}
			Set<String> accesions =  getAccessionsForSimple(queryRequest);
			queryRequest.setQuality(originalQuality);
			return accesions;
			
		}

	}

	@Override
	public List<String> sortAccessions(QueryRequest queryRequest, Set<String> accessions) {
		List<String> sortedAccessions = new ArrayList<String>();
		try {

			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			Query query = queryBuilderService.buildQueryForSearchIndexes("entry", "pl_search", queryRequest);
			SearchResult result = this.solrService.executeQuery(query);

			List<SearchResultItem> results = result.getResults();
			for (SearchResultItem res : results) {
				String entry = (String) res.getProperties().get("id");
				sortedAccessions.add(entry);
			}

		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when retrieving accessions");
		}
		return sortedAccessions;
	}
	
	private Set<String> getAccessionsForSimple(QueryRequest queryRequest) {
		Set<String> set = new LinkedHashSet<String>();
		try {
			Query query = this.queryBuilderService.buildQueryForSearchIndexes("entry", "simple", queryRequest);
			SearchResult results = solrService.executeIdQuery(query);
			Map<String, SearchResultFacet> facets = results.getFacets();
			SearchResultFacet srf = facets.get("id");
			for (Pair<String, Long> f : srf.getFoundFacetFields()) {
				String entry = f.getFirst();
				set.add(entry);
			}
		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when retrieving accessions");
		}
		return set;
	}

}
