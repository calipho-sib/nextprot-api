package org.nextprot.api.solr.index;


public class GoldOnlyEntryIndex extends GoldAndSilverEntryIndex {

	// a way to get it easily from everywhere !
	static public final String NAME = "gold-entry";
	
	public GoldOnlyEntryIndex() {
		super(GoldOnlyEntryIndex.NAME, "npentries1gold");
	}
}
