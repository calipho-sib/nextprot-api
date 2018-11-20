package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;
import org.nextprot.api.solr.core.impl.settings.FieldConfigSet;
import org.nextprot.api.solr.core.impl.settings.IndexParameter;
import org.nextprot.api.solr.core.impl.settings.QueryBaseSettings;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SolrCvCore extends SolrCoreBase<CvSolrField> {

	private static final String NAME = "npcvs1";

	public SolrCvCore(String solrServerBaseURL) {

		super(NAME, Alias.Term, solrServerBaseURL);
	}

	@Override
	public CvSolrField[] getSchema() {
		return CvSolrField.values();
	}

	@Override
	public QuerySettings<CvSolrField> getQuerySettings() {

		return new Settings();
	}

	private static class Settings extends QueryBaseSettings<CvSolrField> {

		private Settings() {
			super(new HashSet<>(Arrays.asList(
					CvSolrField.AC,
					CvSolrField.NAME,
					CvSolrField.SYNONYMS,
					CvSolrField.DESCRIPTION,
					CvSolrField.PROPERTIES,
					CvSolrField.FILTERS)));
		}

		@Override
		protected QueryMode setupConfigs(Map<QueryMode, QueryConfiguration<CvSolrField>> configurations, Set<CvSolrField> fieldSet) {

			List<SortConfig<CvSolrField>> sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration<CvSolrField> defaultConfig = newDefaultConfiguration(sortConfigs, fieldSet);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration<CvSolrField> autocompleteConfig = newAutoCompleteConfiguration(defaultConfig);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			return defaultConfig.getMode();
		}

		private List<SortConfig<CvSolrField>> newSortConfigs() {

			return Arrays.asList(
					new SortConfig<>(SortConfig.Criteria.SCORE, Arrays.asList(
							new Pair<>(CvSolrField.SCORE, ORDER.desc),
							new Pair<>(CvSolrField.FILTERS, ORDER.asc))
					),
					new SortConfig<>(SortConfig.Criteria.NAME, Arrays.asList(
							new Pair<>(CvSolrField.NAME_S, ORDER.asc),
							new Pair<>(CvSolrField.FILTERS, ORDER.asc))
					)
			);
		}

		private IndexConfiguration<CvSolrField> newDefaultConfiguration(List<SortConfig<CvSolrField>> sortConfigs, Set<CvSolrField> fieldSet) {

			IndexConfiguration<CvSolrField> defaultConfig = new IndexConfiguration<>(QueryMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet<CvSolrField>(IndexParameter.FL).addAll(fieldSet));

			defaultConfig.addConfigSet(new FieldConfigSet<CvSolrField>(IndexParameter.QF)
					.addWithBoostFactor(CvSolrField.AC, 64)
					.addWithBoostFactor(CvSolrField.NAME, 32)
					.addWithBoostFactor(CvSolrField.SYNONYMS, 32)
					.addWithBoostFactor(CvSolrField.DESCRIPTION, 16)
					.addWithBoostFactor(CvSolrField.PROPERTIES, 8)
					.addWithBoostFactor(CvSolrField.OTHER_XREFS, 8));

			defaultConfig.addConfigSet(new FieldConfigSet<CvSolrField>(IndexParameter.PF)
					.addWithBoostFactor(CvSolrField.AC, 640)
					.addWithBoostFactor(CvSolrField.NAME, 320)
					.addWithBoostFactor(CvSolrField.SYNONYMS, 320)
					.addWithBoostFactor(CvSolrField.DESCRIPTION, 160)
					.addWithBoostFactor(CvSolrField.PROPERTIES, 80)
					.addWithBoostFactor(CvSolrField.OTHER_XREFS, 80));

			defaultConfig.addOtherParameter("defType", "edismax")
					.addOtherParameter("facet", "true")
					.addOtherParameter("facet.field", "filters")
					.addOtherParameter("facet.limit", "10")
					.addOtherParameter("facet.method", "enum")
					.addOtherParameter("facet.mincount", "1")
					.addOtherParameter("facet.sort", "count");

			defaultConfig.addSortConfigs(sortConfigs);
			defaultConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return defaultConfig;
		}

		private AutocompleteConfiguration<CvSolrField> newAutoCompleteConfiguration(IndexConfiguration<CvSolrField> configuration) {

			AutocompleteConfiguration<CvSolrField> autocompleteConfig = new AutocompleteConfiguration<>(configuration);

			autocompleteConfig.addOtherParameter("defType", "edismax")
					.addOtherParameter("facet", "true")
					.addOtherParameter("facet.field", "text")
					.addOtherParameter("facet.limit", "10")
					.addOtherParameter("facet.method", "enum")
					.addOtherParameter("facet.mincount", "1")
					.addOtherParameter("facet.sort", "count")
					.addOtherParameter("stopwords", "true");

			return autocompleteConfig;
		}
	}
}
