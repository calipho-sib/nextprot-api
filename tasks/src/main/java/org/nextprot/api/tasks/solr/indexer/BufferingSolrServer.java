package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.client.solrj.SolrServerException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.tasks.solr.SimpleHttpSolrServer;
import org.nextprot.api.tasks.solr.SimpleSolrServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Add a certain amount of solr objects to internal buffer before starting to commit to the solr server
 */
public class BufferingSolrServer {

	private static final int BUFFER_SIZE = 150;

	private final SimpleSolrServer solrServer;
	private final List<SolrObject> buffer;
	private final int bufferSize;

    public BufferingSolrServer(String solrServerUrl) {
        this(new SimpleHttpSolrServer(solrServerUrl), BUFFER_SIZE);
    }

	public BufferingSolrServer(SimpleSolrServer solrServer, int bufferSize) {

        this.solrServer = solrServer;
        this.bufferSize = bufferSize;
		buffer = new ArrayList<>();
	}

	/**
	 * Put solr object into buffer - if buffer is full objects are flushed to solr server
	 * @param solrObject object to be indexed by solr
	 */
	public void pushSolrObject(SolrObject solrObject) {

		if (solrObject == null) {
			throw new NextProtException("cannot create solr index from undefined object");
		}

		buffer.add(solrObject);
		if (buffer.size() % bufferSize == 0) {
			flushSolrDocumentsToSolr();
		}
	}

	/**
	 * Commit solr document for indexation by solr server
	 */
	public void commitIndexation() {

		if (!buffer.isEmpty()) {
			flushSolrDocumentsToSolr();
		}

		try {
            solrServer.commit();
            solrServer.optimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	/**
	 * Delete all documents indexes
	 */
	public void clearIndexes() {
		try {
			solrServer.deleteByQuery("*:*");
            solrServer.commit();
            solrServer.optimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

    private void flushSolrDocumentsToSolr() {

        try {
            solrServer.add(buffer.stream()
                .map(so -> so.solrDocument())
	            .collect(Collectors.toList()));
	        buffer.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
