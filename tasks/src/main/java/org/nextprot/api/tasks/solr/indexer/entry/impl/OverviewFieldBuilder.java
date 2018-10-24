package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;

@EntryFieldBuilder
public class OverviewFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		Overview ovv = entry.getOverview();
		String id = entry.getUniqueName();

		addField(EntryField.ID, id);
		addField(EntryField.IDSP0, id);
		addField(EntryField.RECOMMENDED_AC, id.substring(3));
		
		addField(EntryField.PE_LEVEL, ovv.getProteinExistences().getProteinExistence().getLevel());
		addField(EntryField.PROTEIN_EXISTENCE, ovv.getProteinExistences().getProteinExistence().getDescriptionName());

		String precname = ovv.getMainProteinName();
		addField(EntryField.RECOMMENDED_NAME, precname);
		addField(EntryField.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ID, EntryField.IDSP0, EntryField.PE_LEVEL, EntryField.RECOMMENDED_AC, EntryField.PROTEIN_EXISTENCE, EntryField.RECOMMENDED_NAME, EntryField.RECOMMENDED_NAME_S);
	}

}
