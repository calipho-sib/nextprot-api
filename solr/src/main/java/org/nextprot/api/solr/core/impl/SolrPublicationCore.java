package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.schema.PublicationSolrField;
import org.nextprot.api.solr.core.impl.settings.FieldConfigSet;
import org.nextprot.api.solr.core.impl.settings.IndexParameter;
import org.nextprot.api.solr.core.impl.settings.QueryBaseSettings;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SolrPublicationCore extends SolrCoreBase<PublicationSolrField> {

	private static final String NAME = "nppublications1";

	public SolrPublicationCore(String solrServerBaseURL) {

		super(NAME, Alias.Publication, solrServerBaseURL, Collections.emptySet());
	}

	public SolrPublicationCore(String solrServerBaseURL, Set<PublicationSolrField> fieldSet) {

		super(NAME, Alias.Publication, solrServerBaseURL, fieldSet);
	}

	@Override
	public PublicationSolrField[] getSchema() {
		return PublicationSolrField.values();
	}

	@Override
	protected QuerySettings<PublicationSolrField> buildSettings(Set<PublicationSolrField> specificFieldSet) {

		return (specificFieldSet.isEmpty()) ? new Settings() : new Settings(specificFieldSet);
	}

	private static class Settings extends QueryBaseSettings<PublicationSolrField> {

		private Settings() {
			super(new HashSet<>(Arrays.asList(
					PublicationSolrField.ID,
					PublicationSolrField.AC,
					PublicationSolrField.YEAR,
					PublicationSolrField.TITLE,
					PublicationSolrField.FIRST_PAGE,
					PublicationSolrField.LAST_PAGE,
					PublicationSolrField.ABSTRACT,
					PublicationSolrField.VOLUME, // to be used for for display & search
					PublicationSolrField.TYPE,
					PublicationSolrField.PRETTY_JOURNAL, // contains only iso abbr to be displayed
					//PublicationSolrField.SOURCE,
					PublicationSolrField.PRETTY_AUTHORS,
					PublicationSolrField.FILTERS)));
		}

		private Settings(Set<PublicationSolrField> fieldSet) {

			super(fieldSet);
		}

		@Override
		protected QueryMode setupConfigs(Map<QueryMode, QueryConfiguration<PublicationSolrField>> configurations) {

			Map<SortConfig.Criteria, SortConfig<PublicationSolrField>> sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration<PublicationSolrField> defaultConfig = newDefaultConfiguration(sortConfigs);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration<PublicationSolrField> autocompleteConfig = newAutoCompleteConfiguration(defaultConfig);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			return defaultConfig.getMode();
		}

		private Map<SortConfig.Criteria, SortConfig<PublicationSolrField>> newSortConfigs() {

            Map<SortConfig.Criteria, SortConfig<PublicationSolrField>> map = new HashMap<>();

            map.put(SortConfig.Criteria.SCORE, new SortConfig<>(Arrays.asList(
                    new SortConfig.SortBy<>(PublicationSolrField.YEAR, ORDER.desc),
                    new SortConfig.SortBy<>(PublicationSolrField.PRETTY_JOURNAL, ORDER.asc),
                    new SortConfig.SortBy<>(PublicationSolrField.VOLUME_S, ORDER.asc),  // do not use VOLUME cos text_split0 (tokenized field) is not sortable !
                    new SortConfig.SortBy<>(PublicationSolrField.FIRST_PAGE, ORDER.asc))
                    ));

            return map;
		}

		private IndexConfiguration<PublicationSolrField> newDefaultConfiguration(Map<SortConfig.Criteria, SortConfig<PublicationSolrField>> sortConfigs) {

			IndexConfiguration<PublicationSolrField> defaultConfig = new IndexConfiguration<>(QueryMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet<PublicationSolrField>(IndexParameter.FL).addAll(getReturnedFields()));

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

		private AutocompleteConfiguration<PublicationSolrField> newAutoCompleteConfiguration(IndexConfiguration<PublicationSolrField> configuration) {

			AutocompleteConfiguration<PublicationSolrField> autocompleteConfig = new AutocompleteConfiguration<>(configuration);

			autocompleteConfig
					.addOtherParameter("facet.field", "text")
					.addOtherParameter("stopwords", "true");

			return autocompleteConfig;
		}
	}
}
