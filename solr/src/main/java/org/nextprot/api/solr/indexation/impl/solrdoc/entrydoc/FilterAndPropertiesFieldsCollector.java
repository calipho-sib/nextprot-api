package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


@Service
public class FilterAndPropertiesFieldsCollector extends EntrySolrFieldCollector {

	@Autowired
	private EntryReportStatsService entryReportStatsService;

	@Override
	public void collect(Map<EntrySolrField, Object> fields, Entry entry, boolean gold) {

		EntryReportStats ers = entryReportStatsService.reportEntryStats(entry.getUniqueName());

		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		addEntrySolrFieldValue(fields, EntrySolrField.ISOFORM_NUM, ers.countIsoforms());
		int cnt;
		cnt = ers.countPTMs();
		if (cnt > 0) {
			addEntrySolrFieldValue(fields, EntrySolrField.PTM_NUM, cnt);
		}
		cnt = ers.countVariants();
		if (cnt > 0) {
			addEntrySolrFieldValue(fields, EntrySolrField.VAR_NUM, cnt);
		}
		String filters = "";
		if (props.getFilterstructure()) filters += "filterstructure ";
		if (props.getFilterdisease()) filters += "filterdisease ";
		if (props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if (ers.isMutagenesis()) filters += "filtermutagenesis ";
		if (ers.isProteomics()) filters += "filterproteomics ";
		if (filters.length() > 0) {
			addEntrySolrFieldValue(fields, EntrySolrField.FILTERS, filters.trim());
		}
		addEntrySolrFieldValue(fields, EntrySolrField.AA_LENGTH, props.getMaxSeqLen()); // max length among all isoforms
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ISOFORM_NUM, EntrySolrField.PTM_NUM, EntrySolrField.VAR_NUM, EntrySolrField.FILTERS, EntrySolrField.AA_LENGTH);
	}
}
