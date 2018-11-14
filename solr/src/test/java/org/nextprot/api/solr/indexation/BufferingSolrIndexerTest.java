package org.nextprot.api.solr.indexation;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

public class BufferingSolrIndexerTest {

	private SolrIndexationServer indexationServer;

	@Before
	public void setup() {
		indexationServer = mock(SolrIndexationServer.class);
	}

	@Test(expected = NullPointerException.class)
	public void cannotConstructWithouIndexationServer() {

		new BufferingSolrIndexer(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotConstructWithNegativeBufferSize() {

		new BufferingSolrIndexer(indexationServer, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotConstructWithNoBufferSize() {

		new BufferingSolrIndexer(indexationServer, 0);
	}

    @Test
    public void addFactoriesAndDoNotPushForIndexation() throws IOException, SolrServerException {

        BufferingSolrIndexer indexer = new BufferingSolrIndexer(indexationServer, 10);

        indexer.indexDocument(mock(SolrInputDocument.class));
	    indexer.indexDocument(mock(SolrInputDocument.class));
        Assert.assertEquals(2, indexer.getBufferSize());

	    //noinspection unchecked
	    Mockito.verify(indexationServer, never()).indexDocuments(Mockito.any(List.class));
	    Mockito.verify(indexationServer, never()).commitAndOptimize();
    }

	@Test
	public void addOneFactoryAndPushForIndexation() throws IOException, SolrServerException {

		BufferingSolrIndexer indexer = new BufferingSolrIndexer(indexationServer, 1);

		indexer.indexDocument(mock(SolrInputDocument.class));
		Assert.assertEquals(0, indexer.getBufferSize());

		//noinspection unchecked
		Mockito.verify(indexationServer, times(1)).indexDocuments(Mockito.any(List.class));
		Mockito.verify(indexationServer, never()).commitAndOptimize();
	}

    @Test
    public void testPerformIndexationPushBefore() throws IOException, SolrServerException {

	    BufferingSolrIndexer indexer = new BufferingSolrIndexer(indexationServer, 10);

	    indexer.indexDocument(mock(SolrInputDocument.class));
	    indexer.indexAndCommitLastDocuments();
	    Assert.assertEquals(0, indexer.getBufferSize());

	    //noinspection unchecked
	    Mockito.verify(indexationServer, times(1)).indexDocuments(Mockito.any(List.class));
	    Mockito.verify(indexationServer, times(1)).commitAndOptimize();
    }

	@Test
	public void testPerformIndexationNothingToPush() throws IOException, SolrServerException {

		BufferingSolrIndexer indexer = new BufferingSolrIndexer(indexationServer, 10);

		indexer.indexAndCommitLastDocuments();

		//noinspection unchecked
		Mockito.verify(indexationServer, never()).indexDocuments(Mockito.any(List.class));
		Mockito.verify(indexationServer, times(1)).commitAndOptimize();
	}

    @Test
    public void clearIndexes() throws IOException, SolrServerException {

	    BufferingSolrIndexer indexer = new BufferingSolrIndexer(indexationServer, 10);

	    indexer.clearIndexes();

	    Mockito.verify(indexationServer, times(1)).deleteIndexes();
	    Mockito.verify(indexationServer, times(1)).commitAndOptimize();
    }
}