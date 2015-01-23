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

public class CvIndex extends IndexTemplate {

	public CvIndex() {
		super("term", "npcvs1");
	}
	
	public Class<? extends ConfigurationName> getConfigNames() {
		return Configurations.class;
	}


	public Class<? extends IndexField> getFields() {
		return CvField.class;
	}

	@Override
	protected void setupConfigurations() {
		IndexConfiguration defaultConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FL)
				.add(CvField.AC)
				.add(CvField.NAME)
				.add(CvField.SYNONYMS)
				.add(CvField.DESCRIPTION)
				.add(CvField.PROPERTIES)
				.add(CvField.FILTERS)
				.add(CvField.TEXT));
				
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
				.add(CvField.NAME, 32)
				.add(CvField.DESCRIPTION, 16));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
				.add(CvField.NAME, 320)
				.add(CvField.SYNONYMS, 320)
				.add(CvField.DESCRIPTION, 160));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FN)
				.add(CvField.NAME, 1)
				.add(CvField.DESCRIPTION, 1));
		
		defaultConfig.addConfigSet(FieldConfigSet.create(IndexParameter.HI)
				.add(CvField.AC)
				.add(CvField.NAME)
				.add(CvField.DESCRIPTION)
				.add(CvField.PROPERTIES));
		
		defaultConfig.addOtherParameter("defType", "edismax")
			.addOtherParameter("facet", "true")
			.addOtherParameter("facet.field", "filters")
			.addOtherParameter("facet.limit", "10")
			.addOtherParameter("facet.method", "enum")
			.addOtherParameter("facet.mincount", "1")
			.addOtherParameter("facet.sort", "count");
		
		defaultConfig.addSortConfig(SortConfig.create("default", new Pair[] {
				Pair.create(CvField.SCORE, ORDER.desc),
				Pair.create(CvField.FILTERS, ORDER.asc)
		}));		
		defaultConfig.addSortConfig(SortConfig.create("name", new Pair[] {
				Pair.create(CvField.NAME_S, ORDER.asc),
				Pair.create(CvField.FILTERS, ORDER.asc)
		}));		
		defaultConfig.setDefaultSortName("default");
		addConfiguration(defaultConfig);
		setConfigAsDefault(Configurations.SIMPLE);
		
		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(Configurations.AUTOCOMPLETE, defaultConfig);
		
		autocompleteConfig.addOtherParameter("defType", "edismax")
			.addOtherParameter("facet", "true")
			.addOtherParameter("facet.field", "text")
			.addOtherParameter("facet.limit", "10")
			.addOtherParameter("facet.method", "enum")
			.addOtherParameter("facet.mincount", "1")
			.addOtherParameter("facet.sort", "count")
			.addOtherParameter("stopwords", "true");
			
		addConfiguration(autocompleteConfig);
	}
	
	private static class Configurations extends ConfigurationName {
		public static final String SIMPLE = "simple";
		public static final String AUTOCOMPLETE = "autocomplete";
	}
	
	public static enum CvField implements IndexField {
		ID("id"), 
		AC("ac"), 
		NAME("name"), 
		NAME_S("name_s"), 
		SYNONYMS("synonyms"), 
		DESCRIPTION("description"), 
		PROPERTIES("properties"), 
		FILTERS("filters"),
		SCORE("score"), 
		TEXT("text");
		
		private String name;
		
		private CvField(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

}
