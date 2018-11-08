package org.nextprot.api.solr.indexation.service;


public interface SolrIndexingService {
	
	String indexTerminologies();
	String indexPublications();
	String initIndexEntries(boolean isGold);
	String indexEntriesChromosome(boolean isGold, String chrName);
}
