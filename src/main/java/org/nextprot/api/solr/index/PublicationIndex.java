package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.AutocompleteConfiguration;
import org.nextprot.api.solr.FieldConfigSet;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexField;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.IndexTemplate;
import org.nextprot.api.solr.SortConfig;

public class PublicationIndex extends IndexTemplate {
	
	public PublicationIndex() {
		super("publication", "nppublications1");
	}
	
	@Override
	protected void setupConfigurations() {
		IndexConfiguration defaultConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FL)
				.add(PubField.ID)
				.add(PubField.AC)
				.add(PubField.DATE)
				.add(PubField.TITLE)
				.add(PubField.FIRST_PAGE)
				.add(PubField.LAST_PAGE)
				.add(PubField.ABSTRACT)
				.add(PubField.VOLUME)
				.add(PubField.TYPE)
				.add(PubField.JOURNAL)
				.add(PubField.SOURCE)
				.add(PubField.AUTHORS)
				.add(PubField.FILTERS));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
				.add(PubField.AC, 16)
				.add(PubField.DATE, 16)
				.add(PubField.TITLE, 16)
				.add(PubField.ABSTRACT, 16)
				.add(PubField.TYPE, 16)
				.add(PubField.JOURNAL, 8)
				.add(PubField.SOURCE, 8)
				.add(PubField.AUTHORS, 8));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
				.add(PubField.AC, 160)
				.add(PubField.DATE, 160)
				.add(PubField.TITLE, 160)
				.add(PubField.ABSTRACT, 160)
				.add(PubField.TYPE, 160)
				.add(PubField.JOURNAL, 80)
				.add(PubField.SOURCE, 80)
				.add(PubField.AUTHORS, 80));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FN)
				.add(PubField.AC, 1)
				.add(PubField.DATE, 1)
				.add(PubField.TITLE, 1)
				.add(PubField.ABSTRACT, 1)
				.add(PubField.TYPE, 1)
				.add(PubField.JOURNAL, 1)
				.add(PubField.SOURCE, 1)
				.add(PubField.AUTHORS, 1));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.HI)
				.add(PubField.DATE)
				.add(PubField.TITLE)
				.add(PubField.TYPE)
				.add(PubField.JOURNAL)
				.add(PubField.SOURCE)
				.add(PubField.AUTHORS));
		
		defaultConfig.addOtherParameter("defType", "edismax")
			.addOtherParameter("facet", "true")
			.addOtherParameter("facet.field", "filters")
			.addOtherParameter("facet.limit", "10")
			.addOtherParameter("facet.method", "enum")
			.addOtherParameter("facet.mincount", "1")
			.addOtherParameter("facet.sort", "count");
		
		@SuppressWarnings("unchecked")
		SortConfig sortConfig = SortConfig.create("default", new Pair[] {
				Pair.create(PubField.DATE, ORDER.desc),
				Pair.create(PubField.VOLUME, ORDER.desc),
				Pair.create(PubField.FIRST_PAGE, ORDER.desc),
				Pair.create(PubField.TITLE_S, ORDER.asc)
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
	
	public static enum PubField implements IndexField {
		ID("id"), 
		AC("ac"), 
		VOLUME("volume"), 
		FIRST_PAGE("first_page"), 
		LAST_PAGE("last_page"), 
		DATE("date"), 
		TITLE("title"), 
		TITLE_S("title_s"),
		ABSTRACT("abstract"), 
		TYPE("type"), 
		JOURNAL("journal"), 
		SOURCE("source"), 
		AUTHORS("authors"), 
		FILTERS("filters"), 
		TEXT("text");
		
		private String name;
		
		private PubField(String name) {
			this.name = name;
		}
		   
		public String getName() {
			return this.name;
		}
	}

}
