package org.nextprot.api.solr.core;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.config.AutocompleteConfiguration;
import org.nextprot.api.solr.config.FieldConfigSet;
import org.nextprot.api.solr.config.IndexConfiguration;
import org.nextprot.api.solr.config.IndexParameter;
import org.nextprot.api.solr.config.SortConfig;
import org.springframework.stereotype.Component;


@Component
public class PublicationCore extends CoreTemplate {
	
	// a way to get it easily from everywhere !
	public static final String NAME = "publication";

	public PublicationCore() {
		super(PublicationCore.NAME, "nppublications1");
	}

	@Override
	protected IndexConfiguration newDefaultConfiguration() {

		IndexConfiguration defaultConfig = IndexConfiguration.SIMPLE();

		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
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

		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
				.addWithBoostFactor(PublicationSolrField.AC, 16)
				.addWithBoostFactor(PublicationSolrField.YEAR, 16)
				.addWithBoostFactor(PublicationSolrField.TITLE, 16)
				.addWithBoostFactor(PublicationSolrField.ABSTRACT, 4)
				.addWithBoostFactor(PublicationSolrField.VOLUME, 4) // to be used for search (DO NOT USE volume_s which is not a text_split0)
				.addWithBoostFactor(PublicationSolrField.TYPE, 16)
				.addWithBoostFactor(PublicationSolrField.JOURNAL, 8) // contain both full name and iso abbr
				//.add(PubField.SOURCE, 8)
				.addWithBoostFactor(PublicationSolrField.AUTHORS, 8));

		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
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

		defaultConfig.addSortConfig(sortConfigurations);
		defaultConfig.setDefaultSortName("default");

		return defaultConfig;
	}

	@Override
	protected AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration configuration) {

		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(configuration);

		autocompleteConfig
				.addOtherParameter("facet.field", "text")
				.addOtherParameter("stopwords", "true");
		autocompleteConfig.addSortConfig(sortConfigurations);

		return autocompleteConfig;
	}

	@Override
	protected SortConfig[] newSortConfigurations() {

		SortConfig sortConfig = SortConfig.create("default", new Pair[] {
				Pair.create(PublicationSolrField.YEAR, ORDER.desc),
				Pair.create(PublicationSolrField.PRETTY_JOURNAL, ORDER.asc),
				Pair.create(PublicationSolrField.VOLUME_S, ORDER.asc),  // do not use VOLUME cos text_split0 (tokenized field) is not sortable !
				Pair.create(PublicationSolrField.FIRST_PAGE, ORDER.asc),
		});

		return new SortConfig[] { sortConfig };
	}

	@Override
	protected void setupConfigurations() {

		addConfiguration(defaultConfiguration);
		setConfigAsDefault(IndexConfiguration.SIMPLE);
		addConfiguration(autocompleteConfiguration);
	}
	
	@Override
	public SolrField[] getSchema() {
		return PublicationSolrField.values();
	}
}
