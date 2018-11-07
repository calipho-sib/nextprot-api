package org.nextprot.api.solr.core.impl;


import org.nextprot.api.solr.core.SolrCore;
import org.springframework.stereotype.Component;

@Component
public class GoldOnlyEntryCore extends GoldAndSilverEntryCore {

	// a way to get it easily from everywhere !
	private static final String NAME = "npentries1gold";
	
	public GoldOnlyEntryCore() {
		super(NAME, SolrCore.Entity.GoldEntry);
	}
}
