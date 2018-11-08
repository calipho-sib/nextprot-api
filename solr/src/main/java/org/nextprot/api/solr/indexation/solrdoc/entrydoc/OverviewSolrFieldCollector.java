package org.nextprot.api.solr.indexation.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.core.EntrySolrField;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class OverviewSolrFieldCollector extends EntrySolrFieldCollector {

	@Override
	public void collect(Entry entry, boolean gold) {

		Overview ovv = entry.getOverview();
		String id = entry.getUniqueName();

		addEntrySolrFieldValue(EntrySolrField.ID, id);
		addEntrySolrFieldValue(EntrySolrField.IDSP0, id);
		addEntrySolrFieldValue(EntrySolrField.RECOMMENDED_AC, id.substring(3));
		
		addEntrySolrFieldValue(EntrySolrField.PE_LEVEL, ovv.getProteinExistences().getProteinExistence().getLevel());
		addEntrySolrFieldValue(EntrySolrField.PROTEIN_EXISTENCE, ovv.getProteinExistences().getProteinExistence().getDescriptionName());

		String precname = ovv.getMainProteinName();
		addEntrySolrFieldValue(EntrySolrField.RECOMMENDED_NAME, precname);
		addEntrySolrFieldValue(EntrySolrField.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ID, EntrySolrField.IDSP0, EntrySolrField.PE_LEVEL, EntrySolrField.RECOMMENDED_AC, EntrySolrField.PROTEIN_EXISTENCE, EntrySolrField.RECOMMENDED_NAME, EntrySolrField.RECOMMENDED_NAME_S);
	}

}
