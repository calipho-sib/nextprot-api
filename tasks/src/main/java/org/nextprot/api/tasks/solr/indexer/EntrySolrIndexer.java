package org.nextprot.api.tasks.solr.indexer;


public class EntrySolrIndexer extends EntryBaseSolrIndexer {

	public EntrySolrIndexer(String url) {
		super(url, false); // isGold=false -> no filter on annotation quality
	}
}
