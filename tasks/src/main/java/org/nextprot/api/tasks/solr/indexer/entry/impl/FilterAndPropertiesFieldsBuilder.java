package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.Arrays;
import java.util.Collection;


@EntryFieldBuilder
public class FilterAndPropertiesFieldsBuilder extends FieldBuilder{
	
	@Override
	protected void init(Entry entry) {

		EntryReportStats ers = entryReportStatsService.reportEntryStats(entry.getUniqueName());

		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		addField(EntryField.ISOFORM_NUM, ers.countIsoforms());
		int cnt;
		cnt = ers.countPTMs();
		if (cnt > 0) {
			addField(EntryField.PTM_NUM, cnt);
		}
		cnt = ers.countVariants();
		if (cnt > 0) {
			addField(EntryField.VAR_NUM, cnt);
		}
		String filters = "";
		if (props.getFilterstructure()) filters += "filterstructure ";
		if (props.getFilterdisease()) filters += "filterdisease ";
		if (props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if (ers.isMutagenesis()) filters += "filtermutagenesis ";
		if (ers.isProteomics()) filters += "filterproteomics ";
		if (filters.length() > 0) {
			addField(EntryField.FILTERS, filters.trim());
		}
		addField(EntryField.AA_LENGTH, props.getMaxSeqLen()); // max length among all isoforms
	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ISOFORM_NUM, EntryField.PTM_NUM, EntryField.VAR_NUM, EntryField.FILTERS, EntryField.AA_LENGTH);
	}
}
