package org.nextprot.api.solr.core.impl;


import org.nextprot.api.solr.core.impl.schema.EntrySolrField;

import java.util.Set;

public class SolrGoldOnlyEntryCore extends SolrGoldAndSilverEntryCore {

	// a way to get it easily from everywhere !
	private static final String NAME = "npentries1gold";

	public SolrGoldOnlyEntryCore(String solrServerBaseURL) {

		super(NAME, Alias.GoldEntry, solrServerBaseURL);
	}

	public SolrGoldOnlyEntryCore(String solrServerBaseURL, Set<EntrySolrField> fieldSet) {

		super(NAME, Alias.GoldEntry, solrServerBaseURL, fieldSet);
	}
}
