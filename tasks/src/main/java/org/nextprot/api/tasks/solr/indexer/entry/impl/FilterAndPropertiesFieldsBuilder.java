package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;


@Service
public class FilterAndPropertiesFieldsBuilder extends EntryFieldBuilder {

	@Autowired
	private EntryReportStatsService entryReportStatsService;

	@Override
	public void collect(Entry entry, boolean gold) {

		EntryReportStats ers = entryReportStatsService.reportEntryStats(entry.getUniqueName());

		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		addEntryFieldValue(EntryField.ISOFORM_NUM, ers.countIsoforms());
		int cnt;
		cnt = ers.countPTMs();
		if (cnt > 0) {
			addEntryFieldValue(EntryField.PTM_NUM, cnt);
		}
		cnt = ers.countVariants();
		if (cnt > 0) {
			addEntryFieldValue(EntryField.VAR_NUM, cnt);
		}
		String filters = "";
		if (props.getFilterstructure()) filters += "filterstructure ";
		if (props.getFilterdisease()) filters += "filterdisease ";
		if (props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if (ers.isMutagenesis()) filters += "filtermutagenesis ";
		if (ers.isProteomics()) filters += "filterproteomics ";
		if (filters.length() > 0) {
			addEntryFieldValue(EntryField.FILTERS, filters.trim());
		}
		addEntryFieldValue(EntryField.AA_LENGTH, props.getMaxSeqLen()); // max length among all isoforms
	}

	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.ISOFORM_NUM, EntryField.PTM_NUM, EntryField.VAR_NUM, EntryField.FILTERS, EntryField.AA_LENGTH);
	}
}
