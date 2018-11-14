package org.nextprot.api.solr.indexation;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Solr server able to add a certain amount of solr objects to internal buffer before performing indexation
 */
public class BufferingSolrIndexer {

	private static final int BUFFER_SIZE = 150;

	private final SolrIndexationServer solrIndexer;
	private final List<SolrInputDocument> buffer;
	private final int bufferSize;

	public BufferingSolrIndexer(SolrIndexationServer solrIndexer) {
		this(solrIndexer, BUFFER_SIZE);
	}

	public BufferingSolrIndexer(SolrIndexationServer solrIndexer, int bufferSize) {

		Preconditions.checkNotNull(solrIndexer);
		Preconditions.checkArgument(bufferSize > 0);

        this.solrIndexer = solrIndexer;
        this.bufferSize = bufferSize;
		buffer = new ArrayList<>();
	}

	/**
	 * Add factories that can produce solr documents into buffer - if buffer is full, factories create solr docs
	 * that are flushed to solr server for indexation
	 * @param document solr document to be indexed by solr
	 */
	public void indexDocument(SolrInputDocument document) {

		if (document == null) {
			throw new NextProtException("cannot create solr index from undefined solr document");
		}

		buffer.add(document);
		if (buffer.size() % bufferSize == 0) {
			flushSolrDocumentsToSolr();
		}
	}

	/**
	 * Commit last solr documents for indexation by solr server
	 */
	public void indexAndCommitLastDocuments() {

		if (!buffer.isEmpty()) {
			flushSolrDocumentsToSolr();
		}

		commitAndOptimize();
	}

	public void clearIndexes() {
		try {
			solrIndexer.deleteIndexes();
			solrIndexer.commitAndOptimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	public void commitAndOptimize() {
		try {
			solrIndexer.commitAndOptimize();
		} catch (SolrServerException | IOException e) {
			throw new NextProtException(e);
		}
	}

	int getBufferSize() {
		return buffer.size();
	}

    private void flushSolrDocumentsToSolr() {

        try {
			solrIndexer.indexDocuments(buffer);
	        buffer.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
