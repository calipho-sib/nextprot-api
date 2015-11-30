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
				.add(PubField.YEAR)
				.add(PubField.TITLE)
				.add(PubField.FIRST_PAGE)
				.add(PubField.LAST_PAGE)
				.add(PubField.ABSTRACT)
				.add(PubField.VOLUME) // to be used for for display & search 
				.add(PubField.TYPE)
				.add(PubField.PRETTY_JOURNAL) // contains only iso abbr to be displayed
				.add(PubField.SOURCE)
				.add(PubField.PRETTY_AUTHORS)				
				.add(PubField.FILTERS));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
				.add(PubField.AC, 16)
				.add(PubField.YEAR, 16)
				.add(PubField.TITLE, 16)
				.add(PubField.ABSTRACT, 4)
				.add(PubField.VOLUME, 4) // to be used for search (DO NOT USE volume_s which is not a text_split0)
				.add(PubField.TYPE, 16)
				.add(PubField.JOURNAL, 8) // contain both full name and iso abbr
				.add(PubField.SOURCE, 8)
				.add(PubField.AUTHORS, 8));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
				.add(PubField.AC, 160)
				.add(PubField.YEAR, 160)
				.add(PubField.TITLE, 160)
				.add(PubField.ABSTRACT, 40)
				.add(PubField.VOLUME, 40)  // to be used for search (DO NOT USE volume_s which is not a text_split0)
				.add(PubField.TYPE, 160)
				.add(PubField.JOURNAL, 80) // contain both full name and iso abbr
				.add(PubField.SOURCE, 80)
				.add(PubField.AUTHORS, 80));
	

		
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
	
	public static enum PubField implements IndexField {
		ID("id"), 
		IDSP0("idsp0"),                   // searchable (text_split0)
		AC("ac"), 
		VOLUME_S("volume_s"),             // sortable (string)
		VOLUME("volume", "volume"),       // searchable, also used for display by UI (text_split0)
		FIRST_PAGE("first_page"), 
		LAST_PAGE("last_page"), 
		YEAR("year","year"), 
		DATE("date"), 
		TITLE("title","title"),           // searchable, displayable (text_split0)
		TITLE_S("title_s"),               // sortable (string)
		ABSTRACT("abstract","abstract"), 
		TYPE("type"), 
		JOURNAL("journal","journal"),     // searchable (text_split0)
		PRETTY_JOURNAL("pretty_journal"), // displayable (string)
		SOURCE("source"), 
		AUTHORS("authors","author"),      // searchable (text_split0)
		PRETTY_AUTHORS("pretty_authors"), // displayable  (also text_split0 but formatted)
		FILTERS("filters"), 
		TEXT("text");
		
		private String name;
		private String publicName;
		
		private PubField(String name) {
			this.name = name;
		}

		private PubField(String name, String publicName) {
			this.name = name;
			this.publicName = publicName;
		}
		   
		public String getName() {
			return this.name;
		}
		
		public boolean hasPublicName() {
			return this.publicName!=null && this.publicName.length()>0;
		}
		
		public String getPublicName() {
			return this.publicName;
		}
	}

}
