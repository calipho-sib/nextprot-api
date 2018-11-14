package org.nextprot.api.solr.core.impl;


public class SolrGoldOnlyEntryCore extends SolrGoldAndSilverEntryCore {

	// a way to get it easily from everywhere !
	private static final String NAME = "npentries1gold";

	public SolrGoldOnlyEntryCore(String solrServerUrl) {

		super(NAME, Alias.GoldEntry, solrServerUrl);
	}
}
