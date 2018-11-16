package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.core.impl.config.FieldConfigSet;
import org.nextprot.api.solr.core.impl.config.IndexConfiguration;
import org.nextprot.api.solr.core.impl.config.IndexParameter;
import org.nextprot.api.solr.core.impl.config.QueryBaseSettings;
import org.nextprot.api.solr.core.impl.config.SortConfig;
import org.nextprot.api.solr.core.impl.schema.PublicationSolrField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SolrPublicationCore extends SolrCoreBase<PublicationSolrField> {

	private static final String NAME = "nppublications1";

	public SolrPublicationCore(String solrServerBaseURL) {

		super(SolrPublicationCore.NAME, Alias.Publication, solrServerBaseURL);
	}

	@Override
	public PublicationSolrField[] getSchema() {
		return PublicationSolrField.values();
	}

	@Override
	public QuerySettings<PublicationSolrField> getQuerySettings() {

		return new Settings();
	}

	private static class Settings extends QueryBaseSettings<PublicationSolrField> {

		@Override
		protected SearchMode setupConfigs(Map<SearchMode, QueryConfiguration<PublicationSolrField>> configurations) {

			List<SortConfig<PublicationSolrField>> sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration<PublicationSolrField> defaultConfig = newDefaultConfiguration(sortConfigs);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration<PublicationSolrField> autocompleteConfig = newAutoCompleteConfiguration(defaultConfig, sortConfigs);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			return defaultConfig.getMode();
		}

		private List<SortConfig<PublicationSolrField>> newSortConfigs() {

			return Collections.singletonList(
					new SortConfig<>(SortConfig.Criteria.SCORE, Arrays.asList(
							new Pair<>(PublicationSolrField.YEAR, ORDER.desc),
							new Pair<>(PublicationSolrField.PRETTY_JOURNAL, ORDER.asc),
							new Pair<>(PublicationSolrField.VOLUME_S, ORDER.asc),  // do not use VOLUME cos text_split0 (tokenized field) is not sortable !
							new Pair<>(PublicationSolrField.FIRST_PAGE, ORDER.asc))
					)
			);
		}

		private IndexConfiguration<PublicationSolrField> newDefaultConfiguration(List<SortConfig<PublicationSolrField>> sortConfigs) {

			IndexConfiguration<PublicationSolrField> defaultConfig = new IndexConfiguration<>(SearchMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet<PublicationSolrField>(IndexParameter.FL)
					.add(PublicationSolrField.ID)
					.add(PublicationSolrField.AC)
					.add(PublicationSolrField.YEAR)
					.add(PublicationSolrField.TITLE)
					.add(PublicationSolrField.FIRST_PAGE)
					.add(PublicationSolrField.LAST_PAGE)
					.add(PublicationSolrField.ABSTRACT)
					.add(PublicationSolrField.VOLUME) // to be used for for display & search
					.add(PublicationSolrField.TYPE)
					.add(PublicationSolrField.PRETTY_JOURNAL) // contains only iso abbr to be displayed
					//.add(PubField.SOURCE)
					.add(PublicationSolrField.PRETTY_AUTHORS)
					.add(PublicationSolrField.FILTERS));

			defaultConfig.addConfigSet(new FieldConfigSet<PublicationSolrField>(IndexParameter.QF)
					.addWithBoostFactor(PublicationSolrField.AC, 16)
					.addWithBoostFactor(PublicationSolrField.YEAR, 16)
					.addWithBoostFactor(PublicationSolrField.TITLE, 16)
					.addWithBoostFactor(PublicationSolrField.ABSTRACT, 4)
					.addWithBoostFactor(PublicationSolrField.VOLUME, 4) // to be used for search (DO NOT USE volume_s which is not a text_split0)
					.addWithBoostFactor(PublicationSolrField.TYPE, 16)
					.addWithBoostFactor(PublicationSolrField.JOURNAL, 8) // contain both full name and iso abbr
					//.add(PubField.SOURCE, 8)
					.addWithBoostFactor(PublicationSolrField.AUTHORS, 8));

			defaultConfig.addConfigSet(new FieldConfigSet<PublicationSolrField>(IndexParameter.PF)
					.addWithBoostFactor(PublicationSolrField.AC, 160)
					.addWithBoostFactor(PublicationSolrField.YEAR, 160)
					.addWithBoostFactor(PublicationSolrField.TITLE, 160)
					.addWithBoostFactor(PublicationSolrField.ABSTRACT, 40)
					.addWithBoostFactor(PublicationSolrField.VOLUME, 40)  // to be used for search (DO NOT USE volume_s which is not a text_split0)
					.addWithBoostFactor(PublicationSolrField.TYPE, 160)
					.addWithBoostFactor(PublicationSolrField.JOURNAL, 80) // contain both full name and iso abbr
					//.add(PubField.SOURCE, 80)
					.addWithBoostFactor(PublicationSolrField.AUTHORS, 80));

			defaultConfig.addOtherParameter("defType", "edismax")
					.addOtherParameter("facet", "true")
					.addOtherParameter("facet.field", "filters")
					.addOtherParameter("facet.limit", "10")
					.addOtherParameter("facet.method", "enum")
					.addOtherParameter("facet.mincount", "1")
					.addOtherParameter("facet.sort", "count");

			defaultConfig
					.addOtherParameter("spellcheck.dictionary", "default")
					.addOtherParameter("spellcheck", "on")
					.addOtherParameter("spellcheck.extendedResults", "true")
					.addOtherParameter("spellcheck.count", "10")
					.addOtherParameter("spellcheck.alternativeTermCount", "5")
					.addOtherParameter("spellcheck.maxResultsForSuggest", "5")
					.addOtherParameter("spellcheck.collate", "true")
					.addOtherParameter("spellcheck.collateExtendedResults", "true")
					.addOtherParameter("spellcheck.maxCollationTries", "5")
					.addOtherParameter("spellcheck.maxCollations", "10")
					.addOtherParameter("mm", "100%");

			defaultConfig.addSortConfigs(sortConfigs);
			defaultConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return defaultConfig;
		}

		private AutocompleteConfiguration<PublicationSolrField> newAutoCompleteConfiguration(IndexConfiguration<PublicationSolrField> configuration,
		                                                                                     List<SortConfig<PublicationSolrField>> sortConfigs) {

			AutocompleteConfiguration<PublicationSolrField> autocompleteConfig = new AutocompleteConfiguration<>(configuration);

			autocompleteConfig
					.addOtherParameter("facet.field", "text")
					.addOtherParameter("stopwords", "true");
			autocompleteConfig.addSortConfigs(sortConfigs);

			return autocompleteConfig;
		}
	}
}
