package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.core.domain.EntryProperties;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.service.EntryPropertiesService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;


@Service
public class FilterAndPropertiesFieldsCollector extends EntrySolrFieldCollector {

	private EntryReportStatsService entryReportStatsService;
	private EntryPropertiesService entryPropertiesService;

	@Autowired
	public FilterAndPropertiesFieldsCollector(EntryReportStatsService entryReportStatsService, EntryPropertiesService entryPropertiesService) {

		this.entryReportStatsService = entryReportStatsService;
		this.entryPropertiesService = entryPropertiesService;
	}

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {

		EntryReportStats ers = entryReportStatsService.reportEntryStats(entryAccession);

		// Filters and entry properties
		EntryProperties props = entryPropertiesService.findEntryProperties(entryAccession);
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
		if (ers.is3D()) filters += "filterstructure ";
		if (ers.isDisease()) filters += "filterdisease ";
		//if (props.getFilterexpressionprofile()) filters += "filterexpressionprofile "; // old NP1 value
		if (ers.isExpression()) filters += "filterexpressionprofile ";                   // new NP2 computed value
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
