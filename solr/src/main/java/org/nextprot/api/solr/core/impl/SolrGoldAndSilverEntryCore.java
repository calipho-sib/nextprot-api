package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.impl.component.SolrCoreBase;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.settings.FieldConfigSet;
import org.nextprot.api.solr.core.impl.settings.IndexParameter;
import org.nextprot.api.solr.core.impl.settings.QueryBaseSettings;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.SearchByIdConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SolrGoldAndSilverEntryCore extends SolrCoreBase<EntrySolrField> {

	private static final String NAME = "npentries1";

	public SolrGoldAndSilverEntryCore(String solrServerBaseURL) {

		this(NAME, Alias.Entry, solrServerBaseURL, Collections.emptySet());
	}

	public SolrGoldAndSilverEntryCore(String solrServerBaseURL, Set<EntrySolrField> fieldSet) {

		this(NAME, Alias.Entry, solrServerBaseURL, fieldSet);
	}

	protected SolrGoldAndSilverEntryCore(String name, Alias alias, String solrServerBaseURL) {

		this(name, alias, solrServerBaseURL, Collections.emptySet());
	}

	public SolrGoldAndSilverEntryCore(String name, Alias alias, String solrServerBaseURL, Set<EntrySolrField> fieldSet) {

		super(name, alias, solrServerBaseURL, fieldSet);
	}

	@Override
	public EntrySolrField[] getSchema() {

		return EntrySolrField.values();
	}

	protected QuerySettings<EntrySolrField> buildSettings(Set<EntrySolrField> specificFieldSet) {

		return (specificFieldSet.isEmpty()) ? new Settings() : new Settings(specificFieldSet);
	}

	private static class Settings extends QueryBaseSettings<EntrySolrField> {

		private Settings() {
			super(new HashSet<>(Arrays.asList(
					EntrySolrField.ID,
					EntrySolrField.EC_NAME,
					EntrySolrField.FILTERS,
					EntrySolrField.RECOMMENDED_AC,
					EntrySolrField.RECOMMENDED_NAME,
					EntrySolrField.CD_ANTIGEN,
					EntrySolrField.INTERNATIONAL_NAME,
					EntrySolrField.UNIPROT_NAME,
					EntrySolrField.RECOMMENDED_GENE_NAMES,
					EntrySolrField.GENE_BAND,
					EntrySolrField.SCORE,
					EntrySolrField.FUNCTION_DESC,
					EntrySolrField.CHR_LOC,
					EntrySolrField.ISOFORM_NUM,
					EntrySolrField.PTM_NUM,
					EntrySolrField.AA_LENGTH,
					EntrySolrField.VAR_NUM,
					EntrySolrField.PROTEIN_EXISTENCE)));
		}

		private Settings(Set<EntrySolrField> fieldSet) {

			super(fieldSet);
		}

		@Override
		protected QueryMode setupConfigs(Map<QueryMode, QueryConfiguration<EntrySolrField>> configurations) {

			Map<SortConfig.Criteria, SortConfig<EntrySolrField>> sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration<EntrySolrField> defaultConfig = newDefaultConfiguration(sortConfigs);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration<EntrySolrField> autocompleteConfig = newAutoCompleteConfiguration(defaultConfig, sortConfigs);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			// id_search
			IndexConfiguration<EntrySolrField> idSearchConfig = newIdSearchConfiguration(defaultConfig);
			configurations.put(idSearchConfig.getMode(), idSearchConfig);

			// pl_search
			IndexConfiguration<EntrySolrField> plSearchConfig = newPlSearchConfiguration(defaultConfig);
			configurations.put(plSearchConfig.getMode(), plSearchConfig);

			return defaultConfig.getMode();
		}

		private Map<SortConfig.Criteria, SortConfig<EntrySolrField>> newSortConfigs() {

			Map<SortConfig.Criteria, SortConfig<EntrySolrField>> map = new HashMap<>();

			map.put(SortConfig.Criteria.GENE, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.RECOMMENDED_GENE_NAMES_S, ORDER.asc)));
			map.put(SortConfig.Criteria.PROTEIN, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.RECOMMENDED_NAME_S, ORDER.asc)));
			map.put(SortConfig.Criteria.FAMILY, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.FAMILY_NAMES_S, ORDER.asc)));
			map.put(SortConfig.Criteria.LENGTH, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.AA_LENGTH, ORDER.asc)));
			map.put(SortConfig.Criteria.AC, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.ID, ORDER.asc)));
			map.put(SortConfig.Criteria.CHROMOSOME, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.CHR_LOC_S, ORDER.asc)));
			map.put(SortConfig.Criteria.SCORE, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.SCORE, ORDER.desc), 100));

			return map;
		}

		private IndexConfiguration<EntrySolrField> newDefaultConfiguration(Map<SortConfig.Criteria, SortConfig<EntrySolrField>> sortConfigs) {

			IndexConfiguration<EntrySolrField> defaultConfig = new IndexConfiguration<>(QueryMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet<EntrySolrField>(IndexParameter.FL).addAll(getReturnedFields()));

			defaultConfig.addConfigSet(new FieldConfigSet<EntrySolrField>(IndexParameter.QF)
					//.add(Fields.ID,64)
					.addWithBoostFactor(EntrySolrField.IDSP0, 64)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_AC, 8)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_NAME, 32)
					.add(EntrySolrField.CD_ANTIGEN)
					.add(EntrySolrField.INTERNATIONAL_NAME)
					.addWithBoostFactor(EntrySolrField.UNIPROT_NAME, 16)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_ACS, 8)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_NAMES, 16)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_GENE_NAMES, 32)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_GENE_NAMES, 8)
					.addWithBoostFactor(EntrySolrField.FAMILY_NAMES, 4)
					.addWithBoostFactor(EntrySolrField.CV_NAMES, 4)
					.addWithBoostFactor(EntrySolrField.CV_SYNONYMS, 4)
					.addWithBoostFactor(EntrySolrField.CV_ANCESTORS, 2)
					.addWithBoostFactor(EntrySolrField.PEPTIDE, 2)
					.addWithBoostFactor(EntrySolrField.ANTIBODY, 2)
					.add(EntrySolrField.TEXT));

			defaultConfig.addConfigSet(new FieldConfigSet<EntrySolrField>(IndexParameter.PF)
					//.add(Fields.ID, 640)
					.addWithBoostFactor(EntrySolrField.IDSP0, 640)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_AC, 80)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_NAME, 320)
					.add(EntrySolrField.CD_ANTIGEN)
					.add(EntrySolrField.INTERNATIONAL_NAME)
					.addWithBoostFactor(EntrySolrField.UNIPROT_NAME, 160)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_ACS, 80)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_NAMES, 160)
					.addWithBoostFactor(EntrySolrField.RECOMMENDED_GENE_NAMES, 320)
					.addWithBoostFactor(EntrySolrField.ALTERNATIVE_GENE_NAMES, 80)
					.addWithBoostFactor(EntrySolrField.FAMILY_NAMES, 40)
					.addWithBoostFactor(EntrySolrField.CV_NAMES, 40)
					.addWithBoostFactor(EntrySolrField.CV_SYNONYMS, 40)
					.addWithBoostFactor(EntrySolrField.CV_ANCESTORS, 20)
					.addWithBoostFactor(EntrySolrField.PEPTIDE, 20)
					.addWithBoostFactor(EntrySolrField.ANTIBODY, 20)
					.addWithBoostFactor(EntrySolrField.TEXT, 20));

			defaultConfig.addOtherParameter("defType", "edismax")
					.addOtherParameter("df", "text")
					.addOtherParameter("mm", "100%")
					.addOtherParameter("lowercaseOperators", "true")
					.addOtherParameter("ps", "3")
					.addOtherParameter("facet", "true")
					.addOtherParameter("facet.field", "filters")
					.addOtherParameter("facet.limit", "10")
					.addOtherParameter("facet.method", "enum")
					.addOtherParameter("facet.mincount", "1")
					.addOtherParameter("facet.prefix", "")
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

		private AutocompleteConfiguration<EntrySolrField> newAutoCompleteConfiguration(IndexConfiguration<EntrySolrField> defaultConfiguration, Map<SortConfig.Criteria, SortConfig<EntrySolrField>> sortConfigs) {

			AutocompleteConfiguration<EntrySolrField> autocompleteConfig = new AutocompleteConfiguration<>(defaultConfiguration);

			autocompleteConfig
					.addOtherParameter("facet.field", "text")
					.addOtherParameter("stopwords", "true");

			autocompleteConfig.addSortConfigs(sortConfigs);

			return autocompleteConfig;
		}

		private IndexConfiguration<EntrySolrField> newIdSearchConfiguration(IndexConfiguration<EntrySolrField> defaultConfiguration) {

			IndexConfiguration<EntrySolrField> idSearchConfig = new SearchByIdConfiguration<>(QueryMode.ID_SEARCH);
			idSearchConfig.addSortConfig(SortConfig.Criteria.SCORE, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.SCORE, ORDER.desc)));
			idSearchConfig.addConfigSet(defaultConfiguration.getFieldConfigSets().get(IndexParameter.FL));
			idSearchConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return idSearchConfig;
		}

		private IndexConfiguration<EntrySolrField> newPlSearchConfiguration(IndexConfiguration<EntrySolrField> defaultConfiguration) {

			IndexConfiguration<EntrySolrField> plSearchConfig = new SearchByIdConfiguration<>(QueryMode.PL_SEARCH);
			plSearchConfig.addSortConfig(SortConfig.Criteria.SCORE, new SortConfig<>(new SortConfig.SortBy<>(EntrySolrField.SCORE, ORDER.desc)));
			plSearchConfig.addConfigSet(defaultConfiguration.getFieldConfigSets().get(IndexParameter.FL));
			plSearchConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			plSearchConfig.addOtherParameter("facet", "true")
					.addOtherParameter("facet.field", "filters")
					.addOtherParameter("facet.limit", "10")
					.addOtherParameter("facet.method", "enum")
					.addOtherParameter("facet.mincount", "1")
					.addOtherParameter("facet.prefix", "")
					.addOtherParameter("facet.sort", "count");

			return plSearchConfig;
		}
	}
}
