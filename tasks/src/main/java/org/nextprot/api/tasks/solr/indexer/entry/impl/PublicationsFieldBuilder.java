package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class PublicationsFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		/*
		// Publications
		List<Publication> publications = entry.getPublications();
		int publi_computed_count = 0;
		int publi_curated_count = 0;
		int publi_large_scale_count = 0;
		for (Publication currpubli : publications) {
			if(currpubli.getIsComputed() == true) publi_computed_count++;
			if(currpubli.getIsCurated() == true) publi_curated_count++;
			if(currpubli.getIsLargeScale() == true) publi_large_scale_count++;
			String title = currpubli.getTitle();
			if(title.length() > 0) doc.addField("publications",title);
			SortedSet<PublicationAuthor> authors = currpubli.getAuthors();
			for (PublicationAuthor currauthor : authors) {
				doc.addField("publications",currauthor.getLastName() + " " + currauthor.getForeName() + " " + currauthor.getInitials());
			}
			Set<DbXref> pubxrefs = currpubli.getDbXrefs();
			for (DbXref pubxref : pubxrefs) {
				String acc =  pubxref.getAccession();
				String db = pubxref.getDatabaseName();
				doc.addField("xrefs", acc + ", " + db + ":" + acc);
			}
		}
		if(publi_computed_count > 0) doc.addField("publi_computed_count", publi_computed_count);
		if(publi_curated_count > 0) doc.addField("publi_curated_count", publi_curated_count);
		if(publi_large_scale_count > 0) doc.addField("publi_large_scale_count", publi_large_scale_count);
		// Now we can compute informational score
		int info_score = 0;
		if(pe_level == 1) info_score=12;
		else if(pe_level == 2) info_score=10;
		else if(pe_level == 3 || pe_level == 4) info_score=8;
		else if(pe_level == 5) info_score=5;
		int coeff = 100*publi_curated_count + 25*publi_computed_count + 10*publi_large_scale_count;
		info_score = coeff * info_score / 10;
		doc.addField("informational_score", info_score);
		*/

	
	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
