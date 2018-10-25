package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.JournalResourceLocator;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.tasks.solr.SimpleHttpSolrServer;
import org.nextprot.api.tasks.solr.SimpleSolrServer;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class PublicationSolrIndexer extends SolrIndexer<Publication>{

    private PublicationService publicationService;

    public PublicationSolrIndexer(String url, PublicationService publicationService) {
        this(new SimpleHttpSolrServer(url), publicationService);
    }

    public PublicationSolrIndexer(SimpleSolrServer solrServer, PublicationService publicationService) {
        super(solrServer);
        this.publicationService = publicationService;
    }

	@Override
	public SolrInputDocument convertToSolrDocument(Publication publi) {

        GlobalPublicationStatistics.PublicationStatistics publicationStats =
                publicationService.getPublicationStatistics(publi.getPublicationId());

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", publi.getPublicationId());
		List<PublicationDbXref> xrefs = publi.getDbXrefs();
		// TODO: this 'ac' field should be renamed 'xrefs'
		if (xrefs != null)
		   // The format is slightly different in current publication indexes vs terminology indexes, check if justified
		   // if yes create an adhoc Publication.convertXrefsToSolrString method
		   { doc.addField("ac",TerminologyUtils.convertXrefsToSolrString(new ArrayList<>(xrefs))); }
		String filters="";
		filters+=((publicationStats.isComputed())?" computed":"");
		filters+=((publicationStats.isCurated())?" curated":""); // Change getIsCurated or set here to 'curated' if computed is false
		filters+=((publicationStats.isLargeScale())?" largescale":"");
		doc.addField("filters", filters);
		doc.addField("title", publi.getTitle());
		doc.addField("title_s", publi.getTitle());

		if (publi.getPublicationDate() != null) {
			doc.addField("date", publi.getPublicationDate());
			doc.addField("year", publi.getPublicationYear());
		}
		doc.addField("first_page", publi.getFirstPage());
		doc.addField("last_page", publi.getLastPage());
		doc.addField("volume", publi.getVolume());
		doc.addField("volume_s", publi.getVolume());
		doc.addField("abstract", publi.getAbstractText());
		doc.addField("type", publi.getPublicationType().name());

		//doc.addField("source", rs.getString("source"));
		//This source feature is either PubMed (99.99%) or UniProt for published articles with no PMID, it is useless for the indexes since
		// another way to get those is to query ac without pubmed ac:(-pubmed)

		if (publi.isLocatedInScientificJournal()) {

			JournalResourceLocator journal = publi.getJournalResourceLocator();
			String jfield = journal.getName();

			if (journal.hasJournalId()) {

				String jabbrev = journal.getAbbrev();
				jfield += " " + jabbrev;

				// TODO: rename "pretty_journal" to "abbrev_journal"
				doc.addField("pretty_journal", jabbrev);
			}

			doc.addField("journal", jfield);
		}
		// no need the following anymore as journal name is now accessible from journal
		//else if(publi.getJournal_from_properties() != null)
		//	doc.addField("journal", publi.getJournal_from_properties());

		SortedSet<PublicationAuthor> authorset = publi.getAuthors();
		if (authorset != null) {
			String toIndex = "";
			String inidotted = "";
			int i = authorset.size();
			StringBuilder sb = new StringBuilder();
			// (select string_agg(nextprot.pubauthors.last_name||' '|| nextprot.pubauthors.fore_name || ' ' || regexp_replace(nextprot.pubauthors.initials,'(.)',E'\\1.','g'),' | ')  -- PUBLI CONCAT AUTHORS 

			for (PublicationAuthor author : authorset) {
				inidotted = author.getInitials().replaceAll("(.)", "$1\\."); // replace each character by itself plus a dot
				//System.err.println("LastName: " + author.getLastName() + " ForeName: " + author.getForeName() + " Initials: " + author.getInitials() + " inidotted: " + inidotted);
				toIndex = author.getLastName() + " " + author.getForeName() + " " + inidotted;
				doc.addField("authors",toIndex.trim().replaceAll("  ", " "));
				sb.append(author.getLastName() + " " + inidotted.replaceAll("\\.\\.\\.", "."));
				if (--i != 0) sb.append(" | ");
				}
			doc.addField("pretty_authors",sb.toString()); // for display only
		}
		
		return doc;
	}
}
