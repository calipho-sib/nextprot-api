package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.core.impl.config.FieldConfigSet;
import org.nextprot.api.solr.core.impl.config.IndexConfiguration;
import org.nextprot.api.solr.core.impl.config.IndexParameter;
import org.nextprot.api.solr.core.impl.config.QueryBaseConfigurations;
import org.nextprot.api.solr.core.impl.config.SortConfig;
import org.nextprot.api.solr.core.impl.schema.CvSolrField;

import java.util.Arrays;
import java.util.Map;

public class SolrCvCore extends SolrCoreBase {

	private static final String NAME = "npcvs1";

	public SolrCvCore(String solrServerBaseURL) {

		super(NAME, Alias.Term, solrServerBaseURL);
	}

	@Override
	public SolrField[] getSchema() {
		return CvSolrField.values();
	}

	@Override
	public QueryConfigurations getQueryConfigurations() {

		return new Configurations();
	}

	private static class Configurations extends QueryBaseConfigurations {

		@Override
		protected SearchMode setupConfigs(Map<SearchMode, QueryConfiguration> configurations) {

			SortConfig[] sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration defaultConfig = newDefaultConfiguration(sortConfigs);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration autocompleteConfig = newAutoCompleteConfiguration(defaultConfig);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			return defaultConfig.getMode();
		}

		private SortConfig[] newSortConfigs() {

			return new SortConfig[] {
					SortConfig.create(SortConfig.Criteria.SCORE, Arrays.asList(
							Pair.create(CvSolrField.SCORE, ORDER.desc),
							Pair.create(CvSolrField.FILTERS, ORDER.asc))
					),
					SortConfig.create(SortConfig.Criteria.NAME, Arrays.asList(
							Pair.create(CvSolrField.NAME_S, ORDER.asc),
							Pair.create(CvSolrField.FILTERS, ORDER.asc))
					)
			};
		}

		private IndexConfiguration newDefaultConfiguration(SortConfig[] sortConfigs) {

			IndexConfiguration defaultConfig = new IndexConfiguration(SearchMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
					.add(CvSolrField.AC)
					.add(CvSolrField.NAME)
					.add(CvSolrField.SYNONYMS)
					.add(CvSolrField.DESCRIPTION)
					.add(CvSolrField.PROPERTIES)
					.add(CvSolrField.FILTERS));

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
					.addWithBoostFactor(CvSolrField.AC, 64)
					.addWithBoostFactor(CvSolrField.NAME, 32)
					.addWithBoostFactor(CvSolrField.SYNONYMS, 32)
					.addWithBoostFactor(CvSolrField.DESCRIPTION, 16)
					.addWithBoostFactor(CvSolrField.PROPERTIES, 8)
					.addWithBoostFactor(CvSolrField.OTHER_XREFS, 8));

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
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

			defaultConfig.addSortConfig(sortConfigs);
			defaultConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return defaultConfig;
		}

		private AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration configuration) {

			AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(configuration);

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
