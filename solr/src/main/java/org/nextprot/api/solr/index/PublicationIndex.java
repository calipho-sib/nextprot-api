package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.*;


public class PublicationIndex extends IndexTemplate {
	
	// a way to get it easily from everywhere !
	static public final String NAME = "publication";
	
	public PublicationIndex() {
		super(PublicationIndex.NAME, "nppublications1");
	}
	
	@Override
	protected void setupConfigurations() {
		IndexConfiguration defaultConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
				.add(PubField.ID)
				.add(PubField.AC)
				.add(PubField.YEAR)
				.add(PubField.TITLE)
				.add(PubField.FIRST_PAGE)
				.add(PubField.LAST_PAGE)
				.add(PubField.ABSTRACT)
				.add(PubField.VOLUME) // to be used for for display & search
				.add(PubField.TYPE)
				.add(PubField.PRETTY_JOURNAL) // contains only iso abbr to be displayed
				//.add(PubField.SOURCE)
				.add(PubField.PRETTY_AUTHORS)
				.add(PubField.FILTERS));
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
				.addWithBoostFactor(PubField.AC, 16)
				.addWithBoostFactor(PubField.YEAR, 16)
				.addWithBoostFactor(PubField.TITLE, 16)
				.addWithBoostFactor(PubField.ABSTRACT, 4)
				.addWithBoostFactor(PubField.VOLUME, 4) // to be used for search (DO NOT USE volume_s which is not a text_split0)
				.addWithBoostFactor(PubField.TYPE, 16)
				.addWithBoostFactor(PubField.JOURNAL, 8) // contain both full name and iso abbr
				//.add(PubField.SOURCE, 8)
				.addWithBoostFactor(PubField.AUTHORS, 8));
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
				.addWithBoostFactor(PubField.AC, 160)
				.addWithBoostFactor(PubField.YEAR, 160)
				.addWithBoostFactor(PubField.TITLE, 160)
				.addWithBoostFactor(PubField.ABSTRACT, 40)
				.addWithBoostFactor(PubField.VOLUME, 40)  // to be used for search (DO NOT USE volume_s which is not a text_split0)
				.addWithBoostFactor(PubField.TYPE, 160)
				.addWithBoostFactor(PubField.JOURNAL, 80) // contain both full name and iso abbr
				//.add(PubField.SOURCE, 80)
				.addWithBoostFactor(PubField.AUTHORS, 80));
	

		
		defaultConfig.addOtherParameter("defType", "edismax")
			.addOtherParameter("facet", "true")
			.addOtherParameter("facet.field", "filters")
			.addOtherParameter("facet.limit", "10")
			.addOtherParameter("facet.method", "enum")
			.addOtherParameter("facet.mincount", "1")
			.addOtherParameter("facet.sort", "count");
		
		@SuppressWarnings("unchecked")
		SortConfig sortConfig = SortConfig.create("default", new Pair[] {
				Pair.create(PubField.YEAR, ORDER.desc),
				Pair.create(PubField.PRETTY_JOURNAL, ORDER.asc),
				Pair.create(PubField.VOLUME_S, ORDER.asc),  // do not use VOLUME cos text_split0 (tokenized field) is not sortable !
				Pair.create(PubField.FIRST_PAGE, ORDER.asc),
		});

		defaultConfig.addSortConfig(sortConfig);
		defaultConfig.setDefaultSortName("default");
		addConfiguration(defaultConfig);
		setConfigAsDefault(Configurations.SIMPLE);
		
		
		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(Configurations.AUTOCOMPLETE, defaultConfig);
		
		autocompleteConfig
			.addOtherParameter("facet.field", "text")
			.addOtherParameter("stopwords", "true");
		
		autocompleteConfig.addSortConfig(sortConfig);
		addConfiguration(autocompleteConfig);

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
	

		setConfigAsDefault(Configurations.SIMPLE);
		
	}
	
	class Configurations extends ConfigurationName {
		public static final String SIMPLE = "simple";
		public static final String AUTOCOMPLETE = "autocomplete";
	}
	
	public Class<? extends ConfigurationName> getConfigNames() {
		return Configurations.class;
	}

	public Class<? extends IndexField> getFields() {
		return PubField.class;
	}
	
	@Override
	public IndexField[] getFieldValues() {
		return PubField.values();
	}

}
