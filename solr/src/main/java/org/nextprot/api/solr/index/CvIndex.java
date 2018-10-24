package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.*;

public class CvIndex extends IndexTemplate {
	
	// a way to get it easily from everywhere !
	static public final String NAME = "term";
	
	public CvIndex() {
		super(CvIndex.NAME, "npcvs1");
	}

	public Class<? extends IndexField> getFields() {
		return CvField.class;
	}

	@Override
	protected void setupConfigurations() {
		IndexConfiguration defaultConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
				.add(CvField.AC)
				.add(CvField.NAME)
				.add(CvField.SYNONYMS)
				.add(CvField.DESCRIPTION)
				.add(CvField.PROPERTIES)
				.add(CvField.FILTERS));
				//.add(CvField.TEXT));
				
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
				.addWithBoostFactor(CvField.AC, 64)
				.addWithBoostFactor(CvField.NAME, 32)
				.addWithBoostFactor(CvField.SYNONYMS, 32)
				.addWithBoostFactor(CvField.DESCRIPTION, 16)
				.addWithBoostFactor(CvField.PROPERTIES, 8)
				.addWithBoostFactor(CvField.OTHER_XREFS, 8));
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
				.addWithBoostFactor(CvField.AC, 640)
				.addWithBoostFactor(CvField.NAME, 320)
				.addWithBoostFactor(CvField.SYNONYMS, 320)
				.addWithBoostFactor(CvField.DESCRIPTION, 160)
				.addWithBoostFactor(CvField.PROPERTIES, 80)
				.addWithBoostFactor(CvField.OTHER_XREFS, 80));

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
	
	@Override
	public IndexField[] getFieldValues() {
		return CvField.values();
	}
}
