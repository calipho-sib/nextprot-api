package org.nextprot.api.solr.service;


import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;

public interface SolrService {

	/** Clear all entry indexes */
	String initIndexEntries(boolean isGold);

	/** Make indexation of all cv terms */
	String indexTerminologies();

	/** Make indexation of all publications */
	String indexPublications();

	/** Make indexation of all entries located in the given chromosome */
	String indexEntriesChromosome(boolean isGold, String chrName);

	/** Make indexation of one entry */
	String indexEntry(String entryAccession, boolean isGold);

	/** Verifies that the specified name is an existing index */
	boolean checkSolrCore(Entity entity, String quality);

	/** Build a query in autocomplete mode */
	Query buildQueryForAutocomplete(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);

	/** Build a query in search index mode */
	Query buildQueryForSearchIndexes(Entity entity, String configurationName, QueryRequest request);

	/** Build a query in protein list mode */
	Query buildQueryForProteinLists(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter);

	/** Execute a SOLR query and return results */
	SearchResult executeQuery(Query query) throws QueryConfiguration.MissingSortConfigException;

	/** Execute a SOLR query and return only the IDs of the document */
	SearchResult executeIdQuery(Query query);
}
