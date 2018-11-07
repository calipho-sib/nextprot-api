package org.nextprot.api.solr.index;

import org.junit.Test;

import static org.junit.Assert.*;
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

    private SolrIndexer mockSolrIndexer() {

        SolrIndexer indexer = mock(SolrIndexer.class);


        return indexer;
    }
}