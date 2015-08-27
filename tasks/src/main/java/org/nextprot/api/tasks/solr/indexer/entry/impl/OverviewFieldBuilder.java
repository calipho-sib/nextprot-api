package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class OverviewFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		Overview ovv = entry.getOverview();
		String id = entry.getUniqueName();

		putField(Fields.ID, id);
		putField(Fields.IDSP0, id);
		putField(Fields.RECOMMENDED_AC, id.substring(3));
		
		
		putField(Fields.PROTEIN_EXISTENCE, ovv.getProteinExistence());
		int pe_level = ovv.getProteinExistenceLevel(); // Will be used to compute informational score
		putField(Fields.PE_LEVEL, pe_level);
	
		String precname = ovv.getMainProteinName();
		putField(Fields.RECOMMENDED_NAME, precname);
		putField(Fields.RECOMMENDED_NAME_S, precname);

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ID, 
							 Fields.IDSP0, 
							 Fields.RECOMMENDED_AC, 
							 
							 Fields.PROTEIN_EXISTENCE, 
							 
							 Fields.PE_LEVEL, 
							 
							 Fields.RECOMMENDED_NAME, 
							 Fields.RECOMMENDED_NAME_S);
	}

}
