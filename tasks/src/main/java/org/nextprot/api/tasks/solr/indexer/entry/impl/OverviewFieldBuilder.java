package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class OverviewFieldBuilder extends EntryFieldBuilder {

	@Override
	public void collect(Entry entry, boolean gold) {

		Overview ovv = entry.getOverview();
		String id = entry.getUniqueName();

		addEntryFieldValue(EntryField.ID, id);
		addEntryFieldValue(EntryField.IDSP0, id);
		addEntryFieldValue(EntryField.RECOMMENDED_AC, id.substring(3));
		
		addEntryFieldValue(EntryField.PE_LEVEL, ovv.getProteinExistences().getProteinExistence().getLevel());
		addEntryFieldValue(EntryField.PROTEIN_EXISTENCE, ovv.getProteinExistences().getProteinExistence().getDescriptionName());

		String precname = ovv.getMainProteinName();
		addEntryFieldValue(EntryField.RECOMMENDED_NAME, precname);
		addEntryFieldValue(EntryField.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ID, EntryField.IDSP0, EntryField.PE_LEVEL, EntryField.RECOMMENDED_AC, EntryField.PROTEIN_EXISTENCE, EntryField.RECOMMENDED_NAME, EntryField.RECOMMENDED_NAME_S);
	}

}
