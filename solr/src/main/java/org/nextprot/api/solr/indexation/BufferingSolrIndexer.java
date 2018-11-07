package org.nextprot.api.solr.indexation;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrServerException;
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

	private final SolrIndexationServer solrIndexer;
	private final List<SolrDocumentFactory> buffer;
	private final int bufferSize;

	public BufferingSolrIndexer(SolrIndexationServer solrIndexer) {
		this(solrIndexer, BUFFER_SIZE);
	}

	public BufferingSolrIndexer(SolrIndexationServer solrIndexer, int bufferSize) {

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
			solrIndexer.commitIndexation();
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
			solrIndexer.commitIndexation();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	int getBufferSize() {
		return buffer.size();
	}

    private void flushSolrDocumentsToSolr() {

        try {
			solrIndexer.pushDocsForIndexation(buffer.stream()
                .map(SolrDocumentFactory::createSolrInputDocument)
	            .collect(Collectors.toList()));
	        buffer.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
