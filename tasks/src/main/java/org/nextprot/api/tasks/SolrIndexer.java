package org.nextprot.api.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;


abstract class SolrIndexer<T> {
	
	protected HttpSolrServer solrServer;
	protected List<SolrInputDocument> docs;
	
	private static final int BATCH_SIZE = 200;
	
	public SolrIndexer(String url) {
		this.solrServer =  new HttpSolrServer(url);
		this.docs = new ArrayList<SolrInputDocument>();		
	}
	
	public abstract SolrInputDocument convertToSolrDocument(T documentTypes);

	public void add(T t) throws SolrServerException, IOException {
		docs.add(this.convertToSolrDocument(t));
		
		if (docs.size() % BATCH_SIZE == 0) {
			//System.err.println("sent " +docs.size() + " docs to solr so far");
			this.solrServer.add(docs);
			docs.clear();
		}

	}
	
	public void commit() throws SolrServerException, IOException{
		solrServer.commit();
		solrServer.optimize();
	}

	public void deleteByQuery(String dstring) throws SolrServerException, IOException {
		solrServer.deleteByQuery(dstring);
	}

}
