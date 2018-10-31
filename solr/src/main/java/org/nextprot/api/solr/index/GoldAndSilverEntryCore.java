package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.AutocompleteConfiguration;
import org.nextprot.api.solr.CoreTemplate;
import org.nextprot.api.solr.FieldConfigSet;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.SearchByIdConfiguration;
import org.nextprot.api.solr.SolrField;
import org.nextprot.api.solr.SortConfig;

import static org.nextprot.api.solr.SearchByIdConfiguration.ID_SEARCH;
import static org.nextprot.api.solr.SearchByIdConfiguration.PL_SEARCH;

public class GoldAndSilverEntryCore extends CoreTemplate {

	public static final String NAME = "entry";

	public GoldAndSilverEntryCore() {
		this(NAME, "npentries1");
	}

	GoldAndSilverEntryCore(String name, String index) {
		super(name, index);
	}

	public Class<? extends SolrField> getFields() {
		return EntrySolrField.class;
	}

	@Override
	protected void setupConfigurations() {
		
		// SIMPLE Config
		
		IndexConfiguration indexConfig = IndexConfiguration.SIMPLE();
		
		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.FL)
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

		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.QF)
			//.add(Fields.ID,64)
			.addWithBoostFactor(EntrySolrField.IDSP0,64)
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
		
		indexConfig.addConfigSet(new FieldConfigSet(IndexParameter.PF)
			//.add(Fields.ID, 640)
			.addWithBoostFactor(EntrySolrField.IDSP0,640)
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
				SortConfig.create("gene", EntrySolrField.RECOMMENDED_GENE_NAMES_S, ORDER.asc),
				SortConfig.create("protein", EntrySolrField.RECOMMENDED_NAME_S, ORDER.asc),
				SortConfig.create("family", EntrySolrField.FAMILY_NAMES_S, ORDER.asc),
				SortConfig.create("length", EntrySolrField.AA_LENGTH, ORDER.asc),
				SortConfig.create("ac", EntrySolrField.ID, ORDER.asc),
				SortConfig.create("chromosome", EntrySolrField.CHR_LOC_S, ORDER.asc),
				SortConfig.create("default", EntrySolrField.SCORE, ORDER.desc, 100)
		};
		indexConfig.addSortConfig(sortConfigs);
		indexConfig.setDefaultSortName("default");
		addConfiguration(indexConfig);
		
		// AUTOCOMPLETE Config

		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(indexConfig);
		
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
		

		setConfigAsDefault(IndexConfiguration.SIMPLE);
		
		
		// ID_SEARCH Config

		IndexConfiguration idSearchConfig = new SearchByIdConfiguration(ID_SEARCH);
		idSearchConfig.addSortConfig(SortConfig.create("default", EntrySolrField.SCORE, ORDER.desc));
		idSearchConfig.addConfigSet(indexConfig.getFieldConfigSets().get(IndexParameter.FL));
		idSearchConfig.setDefaultSortName("default");
		addConfiguration(idSearchConfig);
		
		
		// PL_SEARCH Config

		IndexConfiguration plSearchConfig = new SearchByIdConfiguration(PL_SEARCH);
		plSearchConfig.addSortConfig(SortConfig.create("default", EntrySolrField.SCORE, ORDER.desc));
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
	public SolrField[] getFieldValues() {
		return EntrySolrField.values();
	}
}
