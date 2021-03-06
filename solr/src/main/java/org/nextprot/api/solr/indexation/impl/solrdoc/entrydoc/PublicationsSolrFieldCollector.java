package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.PublicationAuthor;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.publication.JournalResourceLocator;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

@Service
public class PublicationsSolrFieldCollector extends EntrySolrFieldCollector {
	
	protected Logger logger = Logger.getLogger(PublicationsSolrFieldCollector.class);

	private OverviewService overviewService;
	private PublicationService publicationService;

	// Injecting via constructor to mock service in tests
	@Autowired
	public PublicationsSolrFieldCollector(PublicationService publicationService, OverviewService overviewService) {

		this.publicationService = publicationService;
		this.overviewService = overviewService;
	}

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {

		// Publications
		// Shouldn't Xrefs to PubMed and DOIs be also indexed here ?
		List<Publication> publications = publicationService.findPublicationsByEntryName(entryAccession);
		int publi_computed_count = 0;
		int publi_curated_count = 0;
		int publi_large_scale_count = 0;
		String Jinfo = "";

		for (Publication currpubli : publications) {

			long pubId = currpubli.getPublicationId();
			logger.debug("looking for stats about pair " + entryAccession + " - pubId:" + pubId);
            GlobalPublicationStatistics.PublicationStatistics publiStats = publicationService.getPublicationStatistics(pubId);
            if(publiStats.isComputed()) publi_computed_count++;
			if(publiStats.isCurated()) publi_curated_count++;
			if(publiStats.isLargeScale()) publi_large_scale_count++;

			if(currpubli.isLocatedInScientificJournal()) {

				JournalResourceLocator journalLocator = currpubli.getJournalResourceLocator();

				if (journalLocator.hasJournalId())
					addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS, journalLocator.getNLMid());

				Jinfo = currpubli.getJournalResourceLocator().getName();
				if (journalLocator.hasJournalId())
					Jinfo += " - " + currpubli.getJournalResourceLocator().getMedAbbrev(); // Index name and abbrev in the same token

				addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS,Jinfo);
			}
			String title = currpubli.getTitle();
			if(title.length() > 0) addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS,title);
			SortedSet<PublicationAuthor> authors = currpubli.getAuthors();
			for (PublicationAuthor currauthor : authors) {
				String forename = currauthor.getForeName();
				if(forename.contains(".")) // Submission author
					addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS, currauthor.getLastName() + "  " + currauthor.getInitials());
				else if(!forename.isEmpty() ) // trim not to add spaces when forename/initials are empty
					addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS, (currauthor.getLastName() + " " + forename + " " + currauthor.getInitials()).trim());
				else
					addEntrySolrFieldValue(fields, EntrySolrField.PUBLICATIONS, (currauthor.getLastName() + " " + currauthor.getInitials()).trim());
			}
		}
		
		addEntrySolrFieldValue(fields, EntrySolrField.PUBLI_COMPUTED_COUNT, publi_computed_count);
		addEntrySolrFieldValue(fields, EntrySolrField.PUBLI_CURATED_COUNT, publi_curated_count);
		addEntrySolrFieldValue(fields, EntrySolrField.PUBLI_LARGE_SCALE_COUNT, publi_large_scale_count);

		// Based on the publications and the protein existence level we can compute informational score
		int pe_level = overviewService.findOverviewByEntry(entryAccession).getProteinExistence().getLevel();
		
		float info_score = 0;
		if(pe_level == 1) info_score=12;
		else if(pe_level == 2) info_score=10;
		else if(pe_level == 3 || pe_level == 4) info_score=8;
		else if(pe_level == 5) info_score=5;
		float coeff = 100*publi_curated_count + 25*publi_computed_count + 10*publi_large_scale_count;
		info_score = coeff * info_score / 10;
		addEntrySolrFieldValue(fields, EntrySolrField.INFORMATIONAL_SCORE, info_score);
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.PUBLICATIONS, EntrySolrField.PUBLI_COMPUTED_COUNT, EntrySolrField.PUBLI_CURATED_COUNT, EntrySolrField.PUBLI_LARGE_SCALE_COUNT, EntrySolrField.INFORMATIONAL_SCORE);
	}
}
