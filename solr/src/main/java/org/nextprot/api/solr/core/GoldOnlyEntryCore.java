package org.nextprot.api.solr.core;


import org.springframework.stereotype.Component;

@Component
public class GoldOnlyEntryCore extends GoldAndSilverEntryCore {

	// a way to get it easily from everywhere !
	public static final String NAME = "gold-entry";
	
	public GoldOnlyEntryCore() {
		super(NAME, "npentries1gold");
	}
}
