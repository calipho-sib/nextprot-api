package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.tasks.solr.SimpleSolrServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert any T-object into field and boost information needed to
 * construct solr indices that will be processed by a given solr server
 *
 * @param <T> object type to index
 */
public abstract class SolrIndexer<T> {

	private final SimpleSolrServer solrServer;
	private final List<SolrInputDocument> bufferedSolrDocuments;

	private static final int BATCH_SIZE = 150;

	protected SolrIndexer(SimpleSolrServer solrServer) {
        this.solrServer = solrServer;
		bufferedSolrDocuments = new ArrayList<>();
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
			if(query != null && !query.equals("")) {
                solrServer.deleteByQuery(query);
            }
			else {
                solrServer.deleteByQuery("*:*");
            }
            solrServer.commit();
            solrServer.optimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

    private void flushBufferedDocsToSolr() {

        try {
            solrServer.add(bufferedSolrDocuments);
            bufferedSolrDocuments.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
