package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.AutocompleteConfiguration;
import org.nextprot.api.solr.CoreTemplate;
import org.nextprot.api.solr.FieldConfigSet;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.SolrField;
import org.nextprot.api.solr.SortConfig;

public class CvCore extends CoreTemplate {

	public static final String NAME = "term";

	public static final String SIMPLE = "simple";

	public CvCore() {
		super(CvCore.NAME, "npcvs1");
	}

	@Override
	public Class<? extends SolrField> getFields() {
		return CvSolrField.class;
	}

	@Override
	protected void setupConfigurations() {
		IndexConfiguration defaultConfig = IndexConfiguration.SIMPLE();
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
				.add(CvSolrField.AC)
				.add(CvSolrField.NAME)
				.add(CvSolrField.SYNONYMS)
				.add(CvSolrField.DESCRIPTION)
				.add(CvSolrField.PROPERTIES)
				.add(CvSolrField.FILTERS));
				//.add(CvField.TEXT));
				
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
				.addWithBoostFactor(CvSolrField.AC, 64)
				.addWithBoostFactor(CvSolrField.NAME, 32)
				.addWithBoostFactor(CvSolrField.SYNONYMS, 32)
				.addWithBoostFactor(CvSolrField.DESCRIPTION, 16)
				.addWithBoostFactor(CvSolrField.PROPERTIES, 8)
				.addWithBoostFactor(CvSolrField.OTHER_XREFS, 8));
		
		defaultConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
				.addWithBoostFactor(CvSolrField.AC, 640)
				.addWithBoostFactor(CvSolrField.NAME, 320)
				.addWithBoostFactor(CvSolrField.SYNONYMS, 320)
				.addWithBoostFactor(CvSolrField.DESCRIPTION, 160)
				.addWithBoostFactor(CvSolrField.PROPERTIES, 80)
				.addWithBoostFactor(CvSolrField.OTHER_XREFS, 80));

		defaultConfig.addOtherParameter("defType", "edismax")
			.addOtherParameter("facet", "true")
			.addOtherParameter("facet.field", "filters")
			.addOtherParameter("facet.limit", "10")
			.addOtherParameter("facet.method", "enum")
			.addOtherParameter("facet.mincount", "1")
			.addOtherParameter("facet.sort", "count");
		
		defaultConfig.addSortConfig(SortConfig.create("default", new Pair[] {
				Pair.create(CvSolrField.SCORE, ORDER.desc),
				Pair.create(CvSolrField.FILTERS, ORDER.asc)
		}));		
		defaultConfig.addSortConfig(SortConfig.create("name", new Pair[] {
				Pair.create(CvSolrField.NAME_S, ORDER.asc),
				Pair.create(CvSolrField.FILTERS, ORDER.asc)
		}));		
		defaultConfig.setDefaultSortName("default");
		addConfiguration(defaultConfig);
		setConfigAsDefault(SIMPLE);
		
		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(defaultConfig);
		
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
	
	@Override
	public SolrField[] getFieldValues() {
		return CvSolrField.values();
	}
}
