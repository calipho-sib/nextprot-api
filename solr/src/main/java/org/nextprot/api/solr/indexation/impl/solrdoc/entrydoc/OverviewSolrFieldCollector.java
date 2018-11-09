package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Service
public class OverviewSolrFieldCollector extends EntrySolrFieldCollector {

	@Autowired
	private OverviewService overviewService;

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {

		Overview ovv = overviewService.findOverviewByEntry(entryAccession);

		addEntrySolrFieldValue(fields, EntrySolrField.ID, entryAccession);
		addEntrySolrFieldValue(fields, EntrySolrField.IDSP0, entryAccession);
		addEntrySolrFieldValue(fields, EntrySolrField.RECOMMENDED_AC, entryAccession.substring(3));
		
		addEntrySolrFieldValue(fields, EntrySolrField.PE_LEVEL, ovv.getProteinExistences().getProteinExistence().getLevel());
		addEntrySolrFieldValue(fields, EntrySolrField.PROTEIN_EXISTENCE, ovv.getProteinExistences().getProteinExistence().getDescriptionName());

		String precname = ovv.getMainProteinName();
		addEntrySolrFieldValue(fields, EntrySolrField.RECOMMENDED_NAME, precname);
		addEntrySolrFieldValue(fields, EntrySolrField.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ID, EntrySolrField.IDSP0, EntrySolrField.PE_LEVEL, EntrySolrField.RECOMMENDED_AC, EntrySolrField.PROTEIN_EXISTENCE, EntrySolrField.RECOMMENDED_NAME, EntrySolrField.RECOMMENDED_NAME_S);
	}

}
