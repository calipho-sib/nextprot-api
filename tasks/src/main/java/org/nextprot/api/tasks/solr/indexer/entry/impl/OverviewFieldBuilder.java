package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.index.EntryIndex.Fields;
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

		addField(Fields.ID, id);
		addField(Fields.IDSP0, id);
		addField(Fields.RECOMMENDED_AC, id.substring(3));
		
		addField(Fields.PE_LEVEL, ovv.getProteinExistences().getInferredProteinExistence().getLevel());
		addField(Fields.PROTEIN_EXISTENCE, ovv.getProteinExistences().getInferredProteinExistence().getDescriptionName());

		String precname = ovv.getMainProteinName();
		addField(Fields.RECOMMENDED_NAME, precname);
		addField(Fields.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ID, Fields.IDSP0, Fields.PE_LEVEL, Fields.RECOMMENDED_AC, Fields.PROTEIN_EXISTENCE, Fields.RECOMMENDED_NAME, Fields.RECOMMENDED_NAME_S);
	}

}
