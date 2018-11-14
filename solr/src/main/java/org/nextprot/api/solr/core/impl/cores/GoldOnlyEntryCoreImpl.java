package org.nextprot.api.solr.core.impl.cores;


public class GoldOnlyEntryCoreImpl extends GoldAndSilverEntryCoreImpl {

	// a way to get it easily from everywhere !
	private static final String NAME = "npentries1gold";

	public GoldOnlyEntryCoreImpl(String solrServerUrl) {

		super(NAME, Alias.GoldEntry, solrServerUrl);
	}
}
