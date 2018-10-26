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
 *
 * construct solr indices that will be processed by a given solr server
 */
public class SolrIndexer {

	private final SimpleSolrServer solrServer;
	private final List<SolrObject> bufferedSolrObjects;

	private static final int BATCH_SIZE = 150;

    public SolrIndexer(String solrServerUrl) {
        this(new SimpleHttpSolrServer(solrServerUrl));
    }

	public SolrIndexer(SimpleSolrServer solrServer) {

        this.solrServer = solrServer;
		bufferedSolrObjects = new ArrayList<>();
	}

	public void addToSolr(SolrObject solrObject) {

		if (solrObject == null) return;

		bufferedSolrObjects.add(solrObject);
		if (bufferedSolrObjects.size() % BATCH_SIZE == 0) {
			flushBufferedDocsToSolr();
		}
	}

	public void flushRemainingDocsToSolr() {

		if (!bufferedSolrObjects.isEmpty()) {
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
            solrServer.add(bufferedSolrObjects.stream()
                .map(so -> so.solrDocument())
	            .collect(Collectors.toList()));
            bufferedSolrObjects.clear();
        } catch (SolrServerException | IOException e) {
            throw new NextProtException(e);
        }
    }
}
