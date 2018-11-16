package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.core.impl.config.FieldConfigSet;
import org.nextprot.api.solr.core.impl.config.IndexConfiguration;
import org.nextprot.api.solr.core.impl.config.IndexParameter;
import org.nextprot.api.solr.core.impl.config.QueryBaseConfigurations;
import org.nextprot.api.solr.core.impl.config.SortConfig;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	public QueryConfigurations<CvSolrField> getQueryConfigurations() {

		return new Configurations();
	}

	private static class Configurations extends QueryBaseConfigurations<CvSolrField> {

		@Override
		protected SearchMode setupConfigs(Map<SearchMode, QueryConfiguration<CvSolrField>> configurations) {

			List<SortConfig<CvSolrField>> sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration<CvSolrField> defaultConfig = newDefaultConfiguration(sortConfigs);
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

		private IndexConfiguration<CvSolrField> newDefaultConfiguration(List<SortConfig<CvSolrField>> sortConfigs) {

			IndexConfiguration<CvSolrField> defaultConfig = new IndexConfiguration<>(SearchMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet<CvSolrField>(IndexParameter.FL)
					.add(CvSolrField.AC)
					.add(CvSolrField.NAME)
					.add(CvSolrField.SYNONYMS)
					.add(CvSolrField.DESCRIPTION)
					.add(CvSolrField.PROPERTIES)
					.add(CvSolrField.FILTERS));

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
