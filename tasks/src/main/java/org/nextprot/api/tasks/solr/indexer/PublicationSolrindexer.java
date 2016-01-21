package org.nextprot.api.tasks.solr.indexer;

//import java.util.List;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.CvJournal;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.utils.TerminologyUtils;

public class PublicationSolrindexer extends SolrIndexer<Publication>{


	public PublicationSolrindexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Publication publi) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", publi.getPublicationId());
		Set<DbXref> xrefs = publi.getDbXrefs();
		// TODO: this 'ac' field should be renamed 'xrefs'
		if (xrefs != null)
		   // The format is slightly different in current publication indexes vs terminology indexes, check if justified
		   // if yes create an adhoc Publication.convertXrefsToSolrString method
		   { doc.addField("ac",TerminologyUtils.convertXrefsToSolrString(new ArrayList<DbXref>(xrefs))); }
		String filters="";
		filters+=((publi.getIsComputed())?" computed":"");
		filters+=((publi.getIsCurated())?" curated":""); // Change getIsCurated or set here to 'curated' if computed is false
		filters+=((publi.getIsLargeScale())?" largescale":"");
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
		doc.addField("abstract", publi.getAbstractText());
		doc.addField("type", publi.getPublicationType());
		//doc.addField("source", "?");
		// This source feature is either PubMed (99.99%) or UniProt for published articles with no PMID, it is useless for the indexes since
		// another way to get those is to query ac without pubmed ac:(-pubmed)
		CvJournal journal = publi.getCvJournal();
		if(journal != null) { // TODO: rename "pretty_journal" to "abbrev_journal"
			String jfield = journal.getName();
			String jabbrev = journal.getAbbrev();
			jfield += " " + jabbrev;
			doc.addField("journal", jfield); 
			doc.addField("pretty_journal", jabbrev); 
		}
		else if(publi.getJournal_from_properties() != null)
			doc.addField("journal", publi.getJournal_from_properties()); 

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
