package org.nextprot.api.tasks.solr.indexer.entry.impl;

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

		/*
		String id = entry.getUniqueName();

		//Integer pe_level = 0;
		doc.addField("id", id);
		doc.addField("idsp0", id);
		doc.addField("recommended_ac", id.substring(3));
		Overview ovv = entry.getOverview(); 
		doc.addField("protein_existence", ovv.getProteinExistence());
		int pe_level = ovv.getProteinExistenceLevel(); // Will be used to compute informational score
		doc.addField("pe_level", pe_level);
		//doc.addField("isoform_num", entry.getIsoforms().size());
		String precname = ovv.getMainProteinName();
		//System.err.println(id + " " + precname);
		doc.addField("recommended_name", precname);
		doc.addField("recommended_name_s", precname);
		 */

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
