package org.nextprot.api.solr.indexation;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BufferingSolrIndexerTest {

    @Test
    public void pushSolrDocumentFactory() {

        BufferingSolrIndexer indexer = new BufferingSolrIndexer(mockSolrIndexer(), 10);

        //indexer.pushSolrDocumentFactory();
    }

    @Test
    public void performIndexation() {
    }

    @Test
    public void clearIndexes() {
    }

    private SolrIndexationServer mockSolrIndexer() {

        SolrIndexationServer indexer = mock(SolrIndexationServer.class);


        return indexer;
    }
}