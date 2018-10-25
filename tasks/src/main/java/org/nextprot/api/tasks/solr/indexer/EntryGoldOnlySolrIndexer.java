package org.nextprot.api.tasks.solr.indexer;


public class EntryGoldOnlySolrIndexer extends EntryBaseSolrIndexer {

	public EntryGoldOnlySolrIndexer(String url) {
		super(url, true); // isGold=true -> filter annotations for GOLD quality
	}
}
