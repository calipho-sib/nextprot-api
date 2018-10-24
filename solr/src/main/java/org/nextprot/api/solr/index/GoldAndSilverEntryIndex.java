package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.*;

public class GoldAndSilverEntryIndex extends IndexTemplate {

	// a way to get it easily from everywhere !
	static public final String NAME = "entry";	
	
	public GoldAndSilverEntryIndex() {
		super(GoldAndSilverEntryIndex.NAME, "npentries1");
	}
	
	protected GoldAndSilverEntryIndex(String name, String index) {
		super(name, index);
	}
	
	public Class<? extends IndexField> getFields() {
		return EntryField.class;
	}

	private static class Configurations extends ConfigurationName {
		public static final String SIMPLE = "simple";
		public static final String AUTOCOMPLETE = "autocomplete";
		public static final String ID_SEARCH = "id";
		public static final String PL_SEARCH = "pl_search";
	}

	@Override
	protected void setupConfigurations() {
		
		// SIMPLE Config
		
		IndexConfiguration indexConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
			.add(EntryField.ID)
			.add(EntryField.EC_NAME)
			.add(EntryField.FILTERS)
			.add(EntryField.RECOMMENDED_AC)
			.add(EntryField.RECOMMENDED_NAME)
			.add(EntryField.CD_ANTIGEN)
			.add(EntryField.INTERNATIONAL_NAME)
			.add(EntryField.UNIPROT_NAME)
			.add(EntryField.RECOMMENDED_GENE_NAMES)
			.add(EntryField.GENE_BAND)
			.add(EntryField.SCORE)
			.add(EntryField.FUNCTION_DESC)
			.add(EntryField.CHR_LOC)
			.add(EntryField.ISOFORM_NUM)
			.add(EntryField.PTM_NUM)
			.add(EntryField.AA_LENGTH)
			.add(EntryField.VAR_NUM)
			.add(EntryField.PROTEIN_EXISTENCE));

		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
			//.add(Fields.ID,64)
			.addWithBoostFactor(EntryField.IDSP0,64)
			.addWithBoostFactor(EntryField.RECOMMENDED_AC, 8)
			.addWithBoostFactor(EntryField.RECOMMENDED_NAME, 32)
			.add(EntryField.CD_ANTIGEN)
			.add(EntryField.INTERNATIONAL_NAME)
			.addWithBoostFactor(EntryField.UNIPROT_NAME, 16)
			.addWithBoostFactor(EntryField.ALTERNATIVE_ACS, 8)
			.addWithBoostFactor(EntryField.ALTERNATIVE_NAMES, 16)
			.addWithBoostFactor(EntryField.RECOMMENDED_GENE_NAMES, 32)
			.addWithBoostFactor(EntryField.ALTERNATIVE_GENE_NAMES, 8)
			.addWithBoostFactor(EntryField.FAMILY_NAMES, 4)
			.addWithBoostFactor(EntryField.CV_NAMES, 4)
			.addWithBoostFactor(EntryField.CV_SYNONYMS, 4)
			.addWithBoostFactor(EntryField.CV_ANCESTORS, 2)
			.addWithBoostFactor(EntryField.PEPTIDE, 2)
			.addWithBoostFactor(EntryField.ANTIBODY, 2)
			.add(EntryField.TEXT));
		
		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
			//.add(Fields.ID, 640)
			.addWithBoostFactor(EntryField.IDSP0,640)
			.addWithBoostFactor(EntryField.RECOMMENDED_AC, 80)
			.addWithBoostFactor(EntryField.RECOMMENDED_NAME, 320)
			.add(EntryField.CD_ANTIGEN)
			.add(EntryField.INTERNATIONAL_NAME)
			.addWithBoostFactor(EntryField.UNIPROT_NAME, 160)
			.addWithBoostFactor(EntryField.ALTERNATIVE_ACS, 80)
			.addWithBoostFactor(EntryField.ALTERNATIVE_NAMES, 160)
			.addWithBoostFactor(EntryField.RECOMMENDED_GENE_NAMES, 320)
			.addWithBoostFactor(EntryField.ALTERNATIVE_GENE_NAMES, 80)
			.addWithBoostFactor(EntryField.FAMILY_NAMES, 40)
			.addWithBoostFactor(EntryField.CV_NAMES, 40)
			.addWithBoostFactor(EntryField.CV_SYNONYMS, 40)
			.addWithBoostFactor(EntryField.CV_ANCESTORS, 20)
			.addWithBoostFactor(EntryField.PEPTIDE, 20)
			.addWithBoostFactor(EntryField.ANTIBODY, 20)
			.addWithBoostFactor(EntryField.TEXT, 20));
		
		indexConfig.addOtherParameter("defType", "edismax")
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
		
		SortConfig[] sortConfigs = new SortConfig[] {
				SortConfig.create("gene", EntryField.RECOMMENDED_GENE_NAMES_S, ORDER.asc),
				SortConfig.create("protein", EntryField.RECOMMENDED_NAME_S, ORDER.asc),
				SortConfig.create("family", EntryField.FAMILY_NAMES_S, ORDER.asc),
				SortConfig.create("length", EntryField.AA_LENGTH, ORDER.asc),
				SortConfig.create("ac", EntryField.ID, ORDER.asc),
				SortConfig.create("chromosome", EntryField.CHR_LOC_S, ORDER.asc),
				SortConfig.create("default", EntryField.SCORE, ORDER.desc, 100)
		};
		indexConfig.addSortConfig(sortConfigs);
		indexConfig.setDefaultSortName("default");
		addConfiguration(indexConfig);
		
		// AUTOCOMPLETE Config

		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(Configurations.AUTOCOMPLETE, indexConfig);
		
		autocompleteConfig
			.addOtherParameter("facet.field", "text")
			.addOtherParameter("stopwords", "true");
			
		autocompleteConfig.addSortConfig(sortConfigs);
		addConfiguration(autocompleteConfig);

		indexConfig
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
		
		
		// ID_SEARCH Config

		IndexConfiguration idSearchConfig = new SearchByIdConfiguration(Configurations.ID_SEARCH);
		idSearchConfig.addSortConfig(SortConfig.create("default", EntryField.SCORE, ORDER.desc));
		idSearchConfig.addConfigSet(indexConfig.getFieldConfigSets().get(IndexParameter.FL));
		idSearchConfig.setDefaultSortName("default");
		addConfiguration(idSearchConfig);
		
		
		// PL_SEARCH Config

		IndexConfiguration plSearchConfig = new SearchByIdConfiguration(Configurations.PL_SEARCH);
		plSearchConfig.addSortConfig(SortConfig.create("default", EntryField.SCORE, ORDER.desc));
		plSearchConfig.addConfigSet(indexConfig.getFieldConfigSets().get(IndexParameter.FL));
		plSearchConfig.setDefaultSortName("default");
		plSearchConfig.addSortConfig(sortConfigs);
		
		plSearchConfig.addOtherParameter("facet", "true")
		.addOtherParameter("facet.field", "filters")
		.addOtherParameter("facet.limit", "10")
		.addOtherParameter("facet.method", "enum")
		.addOtherParameter("facet.mincount", "1")
		.addOtherParameter("facet.prefix", "")
		.addOtherParameter("facet.sort", "count");
		addConfiguration(plSearchConfig);
		
	}

	@Override
	public IndexField[] getFieldValues() {
		return EntryField.values();
	}
}
