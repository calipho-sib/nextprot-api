package org.nextprot.api.tasks.service;


public interface SolrIndexingService {
	
	String indexTerminologies();
	String indexPublications();
	String initIndexEntries(boolean isGold);
	String IndexEntriesChromosome(boolean isGold, String chrName);

}
