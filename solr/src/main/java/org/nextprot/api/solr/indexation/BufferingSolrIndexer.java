package org.nextprot.api.solr.indexation;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.solr.indexation.solrdoc.SolrDocumentFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Solr server that add a certain amount of solr objects to internal buffer before performing indexation
 */
public class BufferingSolrIndexer {

	private static final int BUFFER_SIZE = 150;

	private final SolrIndexer solrServer;
	private final List<SolrDocumentFactory> buffer;
	private final int bufferSize;

	public BufferingSolrIndexer(HttpSolrServer httpSolrServer) {
		this(new HttpSolrIndexer(httpSolrServer), BUFFER_SIZE);
	}

	public BufferingSolrIndexer(SolrIndexer solrServer, int bufferSize) {

        this.solrServer = solrServer;
        this.bufferSize = bufferSize;
		buffer = new ArrayList<>();
	}

	/**
	 * Put factories that produce solr document into buffer - if buffer is full objects are flushed to solr server
	 * @param solrDocument object to be indexed by solr
	 */
	public void pushSolrDocumentFactory(SolrDocumentFactory solrDocument) {

		if (solrDocument == null) {
			throw new NextProtException("cannot create solr index from undefined object");
		}

		buffer.add(solrDocument);
		if (buffer.size() % bufferSize == 0) {
			flushSolrDocumentsToSolr();
		}
	}

	/**
	 * Commit solr document for indexation by solr server
	 */
	public void performIndexation() {

		if (!buffer.isEmpty()) {
			flushSolrDocumentsToSolr();
		}

		try {
            solrServer.execute();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	/**
	 * Delete all documents indexes
	 */
	public void clearIndexes() {
		try {
			solrServer.deleteIndexes();
            solrServer.execute();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

    private void flushSolrDocumentsToSolr() {

        try {
            solrServer.performIndexation(buffer.stream()
                .map(so -> so.calcSolrInputDocument())
	            .collect(Collectors.toList()));
	        buffer.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
