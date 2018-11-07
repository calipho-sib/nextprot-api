package org.nextprot.api.solr.index;


public interface SolrIndexationService {
	
	String indexTerminologies();
	String indexPublications();
	String initIndexEntries(boolean isGold);
	String indexEntriesChromosome(boolean isGold, String chrName);
}
