package org.nextprot.api.solr.indexation.solrdoc.entrydoc;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.solr.core.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;


@Service
public class FilterAndPropertiesFieldsCollectorSolr extends EntrySolrFieldCollector {

	@Autowired
	private EntryReportStatsService entryReportStatsService;

	@Override
	public void collect(Entry entry, boolean gold) {

		EntryReportStats ers = entryReportStatsService.reportEntryStats(entry.getUniqueName());

		// Filters and entry properties
		EntryProperties props = entry.getProperties();
		addEntrySolrFieldValue(EntrySolrField.ISOFORM_NUM, ers.countIsoforms());
		int cnt;
		cnt = ers.countPTMs();
		if (cnt > 0) {
			addEntrySolrFieldValue(EntrySolrField.PTM_NUM, cnt);
		}
		cnt = ers.countVariants();
		if (cnt > 0) {
			addEntrySolrFieldValue(EntrySolrField.VAR_NUM, cnt);
		}
		String filters = "";
		if (props.getFilterstructure()) filters += "filterstructure ";
		if (props.getFilterdisease()) filters += "filterdisease ";
		if (props.getFilterexpressionprofile()) filters += "filterexpressionprofile ";
		if (ers.isMutagenesis()) filters += "filtermutagenesis ";
		if (ers.isProteomics()) filters += "filterproteomics ";
		if (filters.length() > 0) {
			addEntrySolrFieldValue(EntrySolrField.FILTERS, filters.trim());
		}
		addEntrySolrFieldValue(EntrySolrField.AA_LENGTH, props.getMaxSeqLen()); // max length among all isoforms
	}

	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.ISOFORM_NUM, EntrySolrField.PTM_NUM, EntrySolrField.VAR_NUM, EntrySolrField.FILTERS, EntrySolrField.AA_LENGTH);
	}
}
