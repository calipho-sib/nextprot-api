package org.nextprot.api.solr.index;


public class GoldEntryIndex extends EntryIndex {

	// a way to get it easily from everywhere !
	static public final String NAME = "gold-entry";
	
	public GoldEntryIndex() {
		super(GoldEntryIndex.NAME, "npentries1gold");
	}
	
}
