package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

@EntryFieldBuilder
public class PublicationsFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		// Publications
		List<Publication> publications = entry.getPublications();
		int publi_computed_count = 0;
		int publi_curated_count = 0;
		int publi_large_scale_count = 0;
		String Jinfo = "";
		
		System.err.println(publications.size() + " publis");
		for (Publication currpubli : publications) {
			if(currpubli.getIsComputed() == true) publi_computed_count++;
			if(currpubli.getIsCurated() == true) publi_curated_count++;
			if(currpubli.getIsLargeScale() == true) publi_large_scale_count++;
			if(currpubli.getJournal() != null) {
				//System.err.println("pubid: " + currpubli.getPublicationId());
				//System.err.println("jid: " + currpubli.getCvJournal().getJournalId());
				addField(Fields.PUBLICATIONS,currpubli.getJournal().getNLMid());
				//if(currpubli.getCvJournal().getName().contains("Nature genetics")) System.err.println("pubid: " + currpubli.getPublicationId());
				Jinfo = currpubli.getJournal().getName() + " - " + currpubli.getJournal().getMedAbbrev(); // Index name and abbrev in the same token
				addField(Fields.PUBLICATIONS,Jinfo);
			//System.err.println(Jinfo);			   
			}
			String title = currpubli.getTitle();
			System.err.println(currpubli.getIsLargeScale() + " " + title);
			if(title.length() > 0) addField(Fields.PUBLICATIONS,title);
			SortedSet<PublicationAuthor> authors = currpubli.getAuthors();
			for (PublicationAuthor currauthor : authors) {
				String forename = currauthor.getForeName();
				if(forename.contains(".")) // Submission author
					addField(Fields.PUBLICATIONS, currauthor.getLastName() + "  " + currauthor.getInitials());
				else
					addField(Fields.PUBLICATIONS, currauthor.getLastName() + " " + currauthor.getForeName() + " " + currauthor.getInitials());
				//if(currauthor.getLastName().contains("Consortium")) System.err.println(currauthor.getLastName());
			}
		}
		
		if(publi_computed_count > 0) addField(Fields.PUBLI_COMPUTED_COUNT, publi_computed_count);
		if(publi_curated_count > 0) addField(Fields.PUBLI_CURATED_COUNT, publi_curated_count);
		if(publi_large_scale_count > 0) addField(Fields.PUBLI_LARGE_SCALE_COUNT, publi_large_scale_count);

		// Based on the publications and the protein existence level we can compute informational score
		int pe_level = entry.getOverview().getProteinExistenceLevel(); 
		
		float info_score = 0;
		if(pe_level == 1) info_score=12;
		else if(pe_level == 2) info_score=10;
		else if(pe_level == 3 || pe_level == 4) info_score=8;
		else if(pe_level == 5) info_score=5;
		float coeff = 100*publi_curated_count + 25*publi_computed_count + 10*publi_large_scale_count;
		info_score = coeff * info_score / 10;
		addField(Fields.INFORMATIONAL_SCORE, info_score);

	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.PUBLICATIONS, Fields.PUBLI_COMPUTED_COUNT, Fields.PUBLI_CURATED_COUNT, Fields.PUBLI_LARGE_SCALE_COUNT, Fields.INFORMATIONAL_SCORE);
	}

}
