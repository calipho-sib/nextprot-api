package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
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
import org.nextprot.api.solr.core.impl.config.SearchByIdConfiguration;
import org.nextprot.api.solr.core.impl.config.SortConfig;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;

import java.util.Map;


public class SolrGoldAndSilverEntryCore extends SolrCoreBase {

	private static final String NAME = "npentries1";

	public SolrGoldAndSilverEntryCore(String solrServerBaseURL) {

		this(NAME, Alias.Entry, solrServerBaseURL);
	}

	protected SolrGoldAndSilverEntryCore(String name, Alias alias, String solrServerBaseURL) {

		super(name, alias, solrServerBaseURL);
	}

	@Override
	public QueryConfigurations getQueryConfigurations() {

		return new Configurations();
	}

	@Override
	public SolrField[] getSchema() {

		return EntrySolrField.values();
	}

	private static class Configurations extends QueryBaseConfigurations {

		@Override
		protected SearchMode setupConfigs(Map<SearchMode, QueryConfiguration> configurations) {

			SortConfig[] sortConfigs = newSortConfigs();

			// Simple
			IndexConfiguration defaultConfig = newDefaultConfiguration(sortConfigs);
			configurations.put(defaultConfig.getMode(), defaultConfig);

			// Autocomplete
			AutocompleteConfiguration autocompleteConfig = newAutoCompleteConfiguration(defaultConfig, sortConfigs);
			configurations.put(autocompleteConfig.getMode(), autocompleteConfig);

			// id_search
			IndexConfiguration idSearchConfig = newIdSearchConfiguration(defaultConfig);
			configurations.put(idSearchConfig.getMode(), idSearchConfig);

			// pl_search
			IndexConfiguration plSearchConfig = newPlSearchConfiguration(defaultConfig);
			configurations.put(plSearchConfig.getMode(), plSearchConfig);

			return defaultConfig.getMode();
		}

		private SortConfig[] newSortConfigs() {

			return new SortConfig[] {
					SortConfig.create(SortConfig.Criteria.GENE, EntrySolrField.RECOMMENDED_GENE_NAMES_S, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.PROTEIN, EntrySolrField.RECOMMENDED_NAME_S, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.FAMILY, EntrySolrField.FAMILY_NAMES_S, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.LENGTH, EntrySolrField.AA_LENGTH, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.AC, EntrySolrField.ID, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.CHROMOSOME, EntrySolrField.CHR_LOC_S, ORDER.asc),
					SortConfig.create(SortConfig.Criteria.SCORE, EntrySolrField.SCORE, ORDER.desc, 100)
			};
		}

		private IndexConfiguration newDefaultConfiguration(SortConfig[] sortConfigs) {

			IndexConfiguration defaultConfig = new IndexConfiguration(SearchMode.SIMPLE);

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
					.add(EntrySolrField.ID)
					.add(EntrySolrField.EC_NAME)
					.add(EntrySolrField.FILTERS)
					.add(EntrySolrField.RECOMMENDED_AC)
					.add(EntrySolrField.RECOMMENDED_NAME)
					.add(EntrySolrField.CD_ANTIGEN)
					.add(EntrySolrField.INTERNATIONAL_NAME)
					.add(EntrySolrField.UNIPROT_NAME)
					.add(EntrySolrField.RECOMMENDED_GENE_NAMES)
					.add(EntrySolrField.GENE_BAND)
					.add(EntrySolrField.SCORE)
					.add(EntrySolrField.FUNCTION_DESC)
					.add(EntrySolrField.CHR_LOC)
					.add(EntrySolrField.ISOFORM_NUM)
					.add(EntrySolrField.PTM_NUM)
					.add(EntrySolrField.AA_LENGTH)
					.add(EntrySolrField.VAR_NUM)
					.add(EntrySolrField.PROTEIN_EXISTENCE));

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
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

			defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
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

			defaultConfig.addSortConfig(sortConfigs);
			defaultConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return defaultConfig;
		}

		private AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration, SortConfig[] sortConfigs) {

			AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(defaultConfiguration);

			autocompleteConfig
					.addOtherParameter("facet.field", "text")
					.addOtherParameter("stopwords", "true");

			autocompleteConfig.addSortConfig(sortConfigs);

			return autocompleteConfig;
		}

		private IndexConfiguration newIdSearchConfiguration(IndexConfiguration defaultConfiguration) {

			IndexConfiguration idSearchConfig = new SearchByIdConfiguration(SearchMode.ID_SEARCH);
			idSearchConfig.addSortConfig(SortConfig.create(SortConfig.Criteria.SCORE, EntrySolrField.SCORE, ORDER.desc));
			idSearchConfig.addConfigSet(defaultConfiguration.getFieldConfigSets().get(IndexParameter.FL));
			idSearchConfig.setDefaultSortCriteria(SortConfig.Criteria.SCORE);

			return idSearchConfig;
		}

		private IndexConfiguration newPlSearchConfiguration(IndexConfiguration defaultConfiguration) {

			IndexConfiguration plSearchConfig = new SearchByIdConfiguration(SearchMode.PL_SEARCH);
			plSearchConfig.addSortConfig(SortConfig.create(SortConfig.Criteria.SCORE, EntrySolrField.SCORE, ORDER.desc));
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
