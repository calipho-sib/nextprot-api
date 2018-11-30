package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.app.ApplicationContextProvider;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.PublicationDbXref;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.JournalResourceLocator;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.core.impl.schema.PublicationSolrField;
import org.nextprot.api.solr.indexation.SolrDocumentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class SolrPublicationDocumentFactory implements SolrDocumentFactory<Publication> {

	@Override
	public SolrInputDocument createSolrInputDocument(Publication publication) {

		NPreconditions.checkNotNull(publication, "unable to create a solr doc from an undefined publication");

		GlobalPublicationStatistics.PublicationStatistics publicationStats =
				ApplicationContextProvider.getApplicationContext().getBean(PublicationService.class)
				        .getPublicationStatistics(publication.getPublicationId());

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(PublicationSolrField.ID.getName(), publication.getPublicationId());
		List<PublicationDbXref> xrefs = publication.getDbXrefs();
		// TODO: this 'ac' field should be renamed 'xrefs'
		if (xrefs != null)
		   // The format is slightly different in current publication indexes vs terminology indexes, check if justified
		   // if yes create an adhoc Publication.convertXrefsToSolrString method
		   { doc.addField(PublicationSolrField.AC.getName(),TerminologyUtils.convertXrefsToSolrString(new ArrayList<>(xrefs))); }
		String filters="";
		filters+=((publicationStats.isComputed())?" computed":"");
		filters+=((publicationStats.isCurated())?" curated":""); // Change getIsCurated or set here to 'curated' if computed is false
		filters+=((publicationStats.isLargeScale())?" largescale":"");
		doc.addField(PublicationSolrField.FILTERS.getName(), filters);
		doc.addField(PublicationSolrField.TITLE.getName(), publication.getTitle());
		doc.addField(PublicationSolrField.TITLE_S.getName(), publication.getTitle());

		if (publication.getPublicationDate() != null) {
			doc.addField(PublicationSolrField.DATE.getName(), publication.getPublicationDate());
			doc.addField(PublicationSolrField.YEAR.getName(), publication.getPublicationYear());
		}
		doc.addField(PublicationSolrField.FIRST_PAGE.getName(), publication.getFirstPage());
		doc.addField(PublicationSolrField.LAST_PAGE.getName(), publication.getLastPage());
		doc.addField(PublicationSolrField.VOLUME.getName(), publication.getVolume());
		doc.addField(PublicationSolrField.VOLUME_S.getName(), publication.getVolume());
		doc.addField(PublicationSolrField.ABSTRACT.getName(), publication.getAbstractText());
		doc.addField(PublicationSolrField.TYPE.getName(), publication.getPublicationType().name());

		//doc.addField("source", rs.getString("source"));
		//This source feature is either PubMed (99.99%) or UniProt for published articles with no PMID, it is useless for the indexes since
		// another way to get those is to query ac without pubmed ac:(-pubmed)

		if (publication.isLocatedInScientificJournal()) {

			JournalResourceLocator journal = publication.getJournalResourceLocator();
			String jfield = journal.getName();

			if (journal.hasJournalId()) {

				String jabbrev = journal.getAbbrev();
				jfield += " " + jabbrev;

				// TODO: rename "pretty_journal" to "abbrev_journal"
				doc.addField(PublicationSolrField.PRETTY_JOURNAL.getName(), jabbrev);
			}

			doc.addField(PublicationSolrField.JOURNAL.getName(), jfield);
		}
		// no need the following anymore as journal name is now accessible from journal
		//else if(publication.getJournal_from_properties() != null)
		//	doc.addField("journal", publication.getJournal_from_properties());

		SortedSet<PublicationAuthor> authorset = publication.getAuthors();
		if (authorset != null) {
			String toIndex;
			String inidotted;
			int i = authorset.size();
			StringBuilder sb = new StringBuilder();
			// (select string_agg(nextprot.pubauthors.last_name||' '|| nextprot.pubauthors.fore_name || ' ' || regexp_replace(nextprot.pubauthors.initials,'(.)',E'\\1.','g'),' | ')  -- PUBLI CONCAT AUTHORS 

			for (PublicationAuthor author : authorset) {
				inidotted = author.getInitials().replaceAll("(.)", "$1\\."); // replace each character by itself plus a dot
				//System.err.println("LastName: " + author.getLastName() + " ForeName: " + author.getForeName() + " Initials: " + author.getInitials() + " inidotted: " + inidotted);
				toIndex = author.getLastName() + " " + author.getForeName() + " " + inidotted;
				doc.addField(PublicationSolrField.AUTHORS.getName(), toIndex.trim().replaceAll("  ", " "));
				sb.append(author.getLastName() + " " + inidotted.replaceAll("\\.\\.\\.", "."));
				if (--i != 0) sb.append(" | ");
				}
			doc.addField(PublicationSolrField.PRETTY_AUTHORS.getName(),sb.toString()); // for display only
		}
		
		return doc;
	}
}
