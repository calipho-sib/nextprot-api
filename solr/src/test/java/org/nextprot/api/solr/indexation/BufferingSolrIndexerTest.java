package org.nextprot.api.solr.indexation;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class BufferingSolrIndexerTest {

    @Test
    public void pushOneSolrDocumentFactory() {

        BufferingSolrIndexer indexer = new BufferingSolrIndexer(mockSolrIndexer(), 10);

        indexer.pushSolrDocumentFactory(mockSolrDocumentFactory());
        Assert.assertEquals(1, indexer.getBufferSize());
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

    private SolrDocumentFactory mockSolrDocumentFactory() {

        SolrDocumentFactory factory = mock(SolrDocumentFactory.class);


        return factory;
    }

}