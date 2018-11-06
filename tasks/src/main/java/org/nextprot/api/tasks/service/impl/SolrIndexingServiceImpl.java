package org.nextprot.api.tasks.service.impl;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrCoreRepository;
import org.nextprot.api.tasks.service.SolrIndexingService;
import org.nextprot.api.tasks.solr.BufferingSolrIndexer;
import org.nextprot.api.tasks.solr.docfactory.SolrCvTermDocumentFactory;
import org.nextprot.api.tasks.solr.docfactory.SolrEntryDocumentFactory;
import org.nextprot.api.tasks.solr.docfactory.SolrPublicationDocumentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Lazy
@Service
public class SolrIndexingServiceImpl implements SolrIndexingService {

    protected Logger logger = Logger.getLogger(SolrIndexingServiceImpl.class);

    @Autowired
    private SolrCoreRepository solrCoreRepository;
    @Autowired
    private TerminologyService terminologyService;
    @Autowired
    private PublicationService publicationService;
    @Autowired
    private GlobalPublicationService globalPublicationService;
    @Autowired
    private EntryBuilderService entryBuilderService;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

	@Override
	public String initIndexEntries(boolean isGold) {

		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();

		SolrCore.Entity entity = isGold ? SolrCore.Entity.GoldEntry : SolrCore.Entity.Entry;
		logAndCollect(info, "initializing index " + entity.getName() + "...STARTING at " + new Date());

		BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(entity, info);

		logAndCollect(info, "clearing index " + entity.getName());
		solrIndexer.clearIndexes();

		logAndCollect(info, "committing index " + entity.getName());
		solrIndexer.performIndexation();

		seconds = (System.currentTimeMillis() / 1000 - seconds);
		logAndCollect(info, "index " + entity.getName() + " initialized in " + seconds + " seconds ...END at " + new Date());

		return info.toString();
	}

    @Override
    public String indexEntriesChromosome(boolean isGold, String chrName) {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();

        SolrCore.Entity entity = isGold ? SolrCore.Entity.GoldEntry : SolrCore.Entity.Entry;
        logAndCollect(info, "adding entries to index " + entity.getName() + " from chromosome " + chrName + "...STARTING at " + new Date());

        BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(entity, info);

        logAndCollect(info, "getting entry list of chromosome " + chrName);
        List<String> allentryids = masterIdentifierService.findUniqueNamesOfChromosome(chrName);

        logAndCollect(info, "start indexing of " + allentryids.size() + " entries");
        int ecnt = 0;
        for (String id : allentryids) {
            ecnt++;

	        solrIndexer.pushSolrDocumentFactory(new SolrEntryDocumentFactory(entryBuilderService.buildWithEverything(id), isGold));

            if ((ecnt % 300) == 0)
                logAndCollect(info, ecnt + "/" + allentryids.size() + " entries added to index " + entity.getName() + " for chromosome " + chrName);
        }

        logAndCollect(info, "committing index " + entity.getName());
	    solrIndexer.performIndexation();

        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, "added entries to index " + entity.getName() + "from chromosome " + chrName + " in " + seconds + " seconds ...END at " + new Date());

        return info.toString();
    }

    @Override
    public String indexTerminologies() {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();
        logAndCollect(info, "terms indexing...STARTING at " + new Date());

        BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(SolrCore.Entity.Term, info);

	    logAndCollect(info, "clearing term index");
	    solrIndexer.clearIndexes();

        logAndCollect(info, "getting terms for all terminologies");
	    List<CvTerm> allterms = terminologyService.findAllCVTerms();

        logAndCollect(info, "start indexing of " + allterms.size() + " terms");
        int termcnt = 0;
        for (CvTerm term : allterms) {
	        solrIndexer.pushSolrDocumentFactory(new SolrCvTermDocumentFactory(term));
            termcnt++;
            if ((termcnt % 3000) == 0)
                logAndCollect(info, termcnt + "/" + allterms.size() + " cv terms done");
        }

        logAndCollect(info, "committing");
	    solrIndexer.performIndexation();
        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, termcnt + " terms indexed in " + seconds + " seconds ...END at " + new Date());

        return info.toString();

    }

    @Override
    public String indexPublications() {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();
        logAndCollect(info, "publications indexing...STARTING at " + new Date());

	    BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(SolrCore.Entity.Publication, info);

	    logAndCollect(info, "clearing publication index");
	    solrIndexer.clearIndexes();

        logAndCollect(info, "getting publications");
        Set<Long> allpubids = globalPublicationService.findAllPublicationIds();

        logAndCollect(info, "start indexing of " + allpubids.size() + " publications");
        int pubcnt = 0;
        for (Long id : allpubids) {
            Publication currpub = publicationService.findPublicationById(id);
            if (currpub.getPublicationType().equals(PublicationType.ARTICLE)) {
                SolrPublicationDocumentFactory solrPublication = new SolrPublicationDocumentFactory(currpub);
	            solrIndexer.pushSolrDocumentFactory(solrPublication);
                pubcnt++;
            }
            if ((pubcnt % 5000) == 0)
                logAndCollect(info, pubcnt + "/" + allpubids.size() + " publications done");
        }

        logAndCollect(info, "committing");
	    solrIndexer.performIndexation();
        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, pubcnt + " publications indexed in " + seconds + " seconds ...END at " + new Date());

        return info.toString();
    }

    private BufferingSolrIndexer newBufferingSolrIndexer(SolrCore.Entity entity, StringBuilder info) {

	    HttpSolrServer solrServer = solrCoreRepository.getSolrCore(entity).newHttpSolrServer();
	    logAndCollect(info, "Solr server: " + solrServer.getBaseURL());

	    return new BufferingSolrIndexer(solrServer);
    }

	private void logAndCollect(StringBuilder info, String message) {
		logger.info(message);
		info.append(message).append("\n");
	}
}
