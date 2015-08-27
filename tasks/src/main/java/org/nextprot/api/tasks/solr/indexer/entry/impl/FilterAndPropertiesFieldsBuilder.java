package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;


@EntryFieldBuilder
public class FilterAndPropertiesFieldsBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry){
		
		/*
		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		doc.addField("isoform_num", props.getIsoformCount());
		int cnt;
		cnt = props.getPtmCount();
		if(cnt > 0) doc.addField("ptm_num", cnt);
		cnt = props.getVarCount();
		if(cnt > 0) doc.addField("var_num", cnt);
		String filters = "";
		if(props.getFilterstructure()) filters += "filterstructure ";
		if(props.getFilterdisease()) filters += "filterdisease ";
		if(props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if(props.getFiltermutagenesis()) filters += "filtermutagenesis ";
		if(props.getFilterproteomics()) filters += "filterproteomics ";
		if(filters.length() > 0) doc.addField("filters", filters);
		doc.addField("aa_length", props.getMaxSeqLen()); // max length among all isoforms
		*/
				
	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}
	


}
