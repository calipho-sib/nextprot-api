package org.nextprot.api.tasks.solr.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NextProtException;

public abstract class SolrIndexer<T> {

	private HttpSolrServer solrServer;
	protected List<SolrInputDocument> docs;

	private static final int BATCH_SIZE = 150;

	public SolrIndexer(String url) {
		this.solrServer = new HttpSolrServer(url);
		this.docs = new ArrayList<SolrInputDocument>();
	}

	public abstract SolrInputDocument convertToSolrDocument(T documentTypes);

	public void add(T t) {

		try {

			docs.add(this.convertToSolrDocument(t));

			if (docs.size() % BATCH_SIZE == 0) {
				// System.err.println("sent " +docs.size() + " docs to solr so far");
				this.solrServer.add(docs);
				docs.clear();

			}

		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}

	}

	public void addRemaing() {
		try {

			if (docs.size() > 0)
			// There are some prepared docs not yet sent to solr server
			{
				solrServer.add(docs);
				docs.clear();
			}

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
