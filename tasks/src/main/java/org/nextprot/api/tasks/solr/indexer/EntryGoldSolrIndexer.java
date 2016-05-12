package org.nextprot.api.tasks.solr.indexer;


public class EntryGoldSolrIndexer extends EntryBaseSolrIndexer {

	public EntryGoldSolrIndexer(String url) {
		super(url, true); // isGold=true -> filter annotations for GOLD quality
	}
}
