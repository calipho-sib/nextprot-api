package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class SolrIndexer<T> {

	private final HttpSolrServer solrServer;
	private final List<SolrInputDocument> bufferedSolrDocuments;

	private static final int BATCH_SIZE = 150;

	public SolrIndexer(String url) {
		this.solrServer = new HttpSolrServer(url);
		this.bufferedSolrDocuments = new ArrayList<>();
	}

	public abstract SolrInputDocument convertToSolrDocument(T documentTypes);

	public void convertAndAddDocsToSolr(T t) {

		SolrInputDocument doc = convertToSolrDocument(t);
		if (doc == null) return;

		bufferedSolrDocuments.add(doc);
		if (bufferedSolrDocuments.size() % BATCH_SIZE == 0) {
			flushBufferedDocsToSolr();
		}
	}

	public void flushRemainingDocsToSolr() {

		if (!bufferedSolrDocuments.isEmpty()) {
			flushBufferedDocsToSolr();
		}
	}

	private void flushBufferedDocsToSolr() {

		try {
			this.solrServer.add(bufferedSolrDocuments);
			bufferedSolrDocuments.clear();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	public void commit() {
		try {

			solrServer.commit();
			solrServer.optimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}

	}

	public void clearDatabase(String query) {
		try {
			if(query != null && !query.equals("")) solrServer.deleteByQuery(query);
			else solrServer.deleteByQuery("*:*");
			solrServer.commit();
			solrServer.optimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}
}
