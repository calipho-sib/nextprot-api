package org.nextprot.api.web.service.impl;

import com.google.common.base.Joiner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.SolrQueryService;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.web.service.QueryBuilderService;
import org.nextprot.api.web.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Lazy
public class SearchServiceImpl implements SearchService {

	private final Log Logger = LogFactory.getLog(SearchServiceImpl.class);

	@Autowired
	private SolrQueryService solrQueryService;

	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;
	@Autowired
	private UserQueryService userQueryService;
	@Autowired
	private UserProteinListService proteinListService;
	@Autowired
	private QueryBuilderService queryBuilderService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Override
	public Set<String> getAccessions(QueryRequest queryRequest) {

		if (queryRequest.hasChromosome()) {
			
			Logger.debug("queryRequest.hasChromosome()");
			return new HashSet<>(this.masterIdentifierService.findUniqueNamesOfChromosome(queryRequest.getChromosome()));

		} else if (queryRequest.hasList()) {
			
			Logger.debug("queryRequest.hasList()");
			UserProteinList proteinList = this.proteinListService.getUserProteinListByPublicId(queryRequest.getListId());
			return proteinList.getAccessionNumbers();

		} else if (queryRequest.hasNextProtQuery()) {
		
			UserQuery uq  = userQueryService.getUserQueryByPublicId(queryRequest.getQueryId());
			return new HashSet<>(sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
		
		} else if (queryRequest.hasSparql()) {
		
			return new HashSet<>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
		
		} else {

			String originalQuality = queryRequest.getQuality();
			//Set gold quality if not specified
			if((queryRequest.getQuality() == null) || (queryRequest.getQuality().equals(""))){
				queryRequest.setQuality("gold");
			}
			Set<String> accessions =  getAccessionsForSimple(queryRequest);
			queryRequest.setQuality(originalQuality);

			return accessions;
		}
	}

	@Override
	public List<String> sortAccessions(QueryRequest queryRequest, Set<String> accessions) {
		List<String> sortedAccessions = new ArrayList<String>();
		try {

			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			Query query = queryBuilderService.buildQueryForSearchIndexes("entry", "pl_search", queryRequest);
			SearchResult result = this.solrQueryService.executeQuery(query);

			List<Map<String, Object>> results = result.getResults();
			for (Map<String, Object> res : results) {
				String entry = (String) res.get("id");
				sortedAccessions.add(entry);
			}

		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when retrieving accessions");
		}
		return sortedAccessions;
	}
	
	private Set<String> getAccessionsForSimple(QueryRequest queryRequest) {
		Set<String> set = new LinkedHashSet<>();
		try {
			Query query = this.queryBuilderService.buildQueryForSearchIndexes("entry", "simple", queryRequest);
			SearchResult results = solrQueryService.executeIdQuery(query);
			for (Map<String, Object> f : results.getFoundFacets("id")) {
				String entry = (String) f.get("name");
				set.add(entry);
			}
		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("Error when retrieving accessions");
		}
		return set;
	}

}
