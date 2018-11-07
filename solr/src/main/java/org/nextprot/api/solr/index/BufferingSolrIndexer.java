package org.nextprot.api.solr.index;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Solr server able to add a certain amount of solr objects to internal buffer before performing indexation
 */
public class BufferingSolrIndexer {

	private static final int BUFFER_SIZE = 150;

	private final SolrIndexer solrIndexer;
	private final List<SolrDocumentFactory> buffer;
	private final int bufferSize;

	public BufferingSolrIndexer(HttpSolrServer httpSolrServer) {
		this(httpSolrServer, BUFFER_SIZE);
	}

	public BufferingSolrIndexer(HttpSolrServer httpSolrServer, int bufferSize) {
		this(new HttpSolrIndexer(httpSolrServer), bufferSize);
	}

	public BufferingSolrIndexer(SolrIndexer solrIndexer, int bufferSize) {

		Preconditions.checkNotNull(solrIndexer);
		Preconditions.checkArgument(bufferSize >= 0);

        this.solrIndexer = solrIndexer;
        this.bufferSize = bufferSize;
		buffer = new ArrayList<>();
	}

	/**
	 * Put factories that produce solr document into buffer - if buffer is full objects are flushed to solr server
	 * @param documentFactory a factory that are able to create solr document from object to be indexed by solr
	 */
	public void pushSolrDocumentFactory(SolrDocumentFactory documentFactory) {

		if (documentFactory == null) {
			throw new NextProtException("cannot create solr index from undefined solr document factory");
		}

		buffer.add(documentFactory);
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
			solrIndexer.execute();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	/**
	 * Delete all documents indexes
	 */
	public void clearIndexes() {
		try {
			solrIndexer.deleteIndexes();
			solrIndexer.execute();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

    private void flushSolrDocumentsToSolr() {

        try {
			solrIndexer.performIndexation(buffer.stream()
                .map(SolrDocumentFactory::createSolrInputDocument)
	            .collect(Collectors.toList()));
	        buffer.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
