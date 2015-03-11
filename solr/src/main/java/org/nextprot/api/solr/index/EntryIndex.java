package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.solr.AutocompleteConfiguration;
import org.nextprot.api.solr.FieldConfigSet;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexField;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.IndexTemplate;
import org.nextprot.api.solr.SearchByIdConfiguration;
import org.nextprot.api.solr.SortConfig;

public class EntryIndex extends IndexTemplate {

	public EntryIndex() {
		super("entry", "npentries1");
	}
	
	protected EntryIndex(String name, String index) {
		super(name, index);
	}
	
	public Class<? extends ConfigurationName> getConfigNames() {
		return Configurations.class;
	}

	public Class<? extends IndexField> getFields() {
		return Fields.class;
	}

	private static class Configurations extends ConfigurationName {
		public static final String SIMPLE = "simple";
		public static final String SIMPLE_IDS = "simple_ids";

		public static final String AUTOCOMPLETE = "autocomplete";
		public static final String ID_SEARCH = "id";
		public static final String PL_SEARCH = "pl_search";

	}

	@Override
	protected void setupConfigurations() {
		
		
		
		  // SIMPLE Config
			IndexConfiguration indexSimpleConfig = new IndexConfiguration(Configurations.SIMPLE);
			
			indexSimpleConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FL)
				.add(Fields.ID)
				.add(Fields.EC_NAME)
				.add(Fields.FILTERS)
				.add(Fields.RECOMMENDED_AC)
				.add(Fields.RECOMMENDED_NAME)
				.add(Fields.UNIPROT_NAME)
				.add(Fields.RECOMMENDED_GENE_NAMES)
				.add(Fields.GENE_BAND)
				.add(Fields.SCORE)
				.add(Fields.FUNCTION_DESC)
				.add(Fields.CHR_LOC)
				.add(Fields.ISOFORM_NUM)
				.add(Fields.PTM_NUM)
				.add(Fields.AA_LENGTH)
				.add(Fields.VAR_NUM)
				.add(Fields.PROTEIN_EXISTENCE));
	
			indexSimpleConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
				.add(Fields.RECOMMENDED_AC, 8)
				.add(Fields.RECOMMENDED_NAME, 32)
				.add(Fields.UNIPROT_NAME, 16)
				.add(Fields.ALTERNATIVE_ACS, 8)
				.add(Fields.ALTERNATIVE_NAMES, 16)
				.add(Fields.RECOMMENDED_GENE_NAMES, 32)
				.add(Fields.ALTERNATIVE_GENE_NAMES, 8)
				.add(Fields.FAMILY_NAMES, 4)
				.add(Fields.CV_NAMES, 4)
				.add(Fields.CV_SYNONYMS, 4)
				.add(Fields.CV_ANCESTORS, 2)
				.add(Fields.PEPTIDE, 2)
				.add(Fields.ANTIBODY, 2)
				.add(Fields.TEXT, 0));
			
			indexSimpleConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
				.add(Fields.RECOMMENDED_AC, 80)
				.add(Fields.RECOMMENDED_NAME, 320)
				.add(Fields.UNIPROT_NAME, 160)
				.add(Fields.ALTERNATIVE_ACS, 80)
				.add(Fields.ALTERNATIVE_NAMES, 160)
				.add(Fields.RECOMMENDED_GENE_NAMES, 320)
				.add(Fields.ALTERNATIVE_GENE_NAMES, 80)
				.add(Fields.FAMILY_NAMES, 40)
				.add(Fields.CV_NAMES, 40)
				.add(Fields.CV_SYNONYMS, 40)
				.add(Fields.CV_ANCESTORS, 20)
				.add(Fields.PEPTIDE, 20)
				.add(Fields.ANTIBODY, 20)
				.add(Fields.TEXT, 20));
			
			indexSimpleConfig.addOtherParameter("defType", "edismax")
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
					SortConfig.create("gene", Fields.RECOMMENDED_GENE_NAMES_S, ORDER.asc),
					SortConfig.create("protein", Fields.RECOMMENDED_NAME_S, ORDER.asc),
					SortConfig.create("family", Fields.FAMILY_NAMES_S, ORDER.asc),
					SortConfig.create("length", Fields.AA_LENGTH, ORDER.asc),
					SortConfig.create("ac", Fields.ID, ORDER.asc),
					SortConfig.create("chromosome", Fields.CHR_LOC_S, ORDER.asc),
					SortConfig.create("default", Fields.SCORE, ORDER.desc, 100)
			};
			indexSimpleConfig.addSortConfig(sortConfigs);
			indexSimpleConfig.setDefaultSortName("default");
			addConfiguration(indexSimpleConfig);
		
		
		
		// SIMPLE_IDS Config (return accessions only)
		{
			IndexConfiguration indexSimplesIdsConfig = new IndexConfiguration(Configurations.SIMPLE_IDS);
			
			indexSimplesIdsConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FL)
				.add(Fields.ID));

			indexSimplesIdsConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
				.add(Fields.RECOMMENDED_AC, 8)
				.add(Fields.RECOMMENDED_NAME, 32)
				.add(Fields.UNIPROT_NAME, 16)
				.add(Fields.ALTERNATIVE_ACS, 8)
				.add(Fields.ALTERNATIVE_NAMES, 16)
				.add(Fields.RECOMMENDED_GENE_NAMES, 32)
				.add(Fields.ALTERNATIVE_GENE_NAMES, 8)
				.add(Fields.FAMILY_NAMES, 4)
				.add(Fields.CV_NAMES, 4)
				.add(Fields.CV_SYNONYMS, 4)
				.add(Fields.CV_ANCESTORS, 2)
				.add(Fields.PEPTIDE, 2)
				.add(Fields.ANTIBODY, 2)
				.add(Fields.TEXT, 0));
			
			indexSimplesIdsConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
				.add(Fields.RECOMMENDED_AC, 80)
				.add(Fields.RECOMMENDED_NAME, 320)
				.add(Fields.UNIPROT_NAME, 160)
				.add(Fields.ALTERNATIVE_ACS, 80)
				.add(Fields.ALTERNATIVE_NAMES, 160)
				.add(Fields.RECOMMENDED_GENE_NAMES, 320)
				.add(Fields.ALTERNATIVE_GENE_NAMES, 80)
				.add(Fields.FAMILY_NAMES, 40)
				.add(Fields.CV_NAMES, 40)
				.add(Fields.CV_SYNONYMS, 40)
				.add(Fields.CV_ANCESTORS, 20)
				.add(Fields.PEPTIDE, 20)
				.add(Fields.ANTIBODY, 20)
				.add(Fields.TEXT, 20));
			
			indexSimplesIdsConfig.addOtherParameter("defType", "edismax")
				.addOtherParameter("df", "text")
				.addOtherParameter("mm", "100%")
				.addOtherParameter("lowercaseOperators", "true")
				.addOtherParameter("ps", "3");
			
			
			indexSimplesIdsConfig.addSortConfig(sortConfigs);
			indexSimplesIdsConfig.setDefaultSortName("default");
			addConfiguration(indexSimplesIdsConfig);

			
		}
		

		
		
		
		// AUTOCOMPLETE Config

		AutocompleteConfiguration autocompleteConfig = new AutocompleteConfiguration(Configurations.AUTOCOMPLETE, indexSimpleConfig);
		
		autocompleteConfig
			.addOtherParameter("facet.field", "text")
			.addOtherParameter("stopwords", "true");
			
		autocompleteConfig.addSortConfig(sortConfigs);
		addConfiguration(autocompleteConfig);

		indexSimpleConfig
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
		idSearchConfig.addSortConfig(SortConfig.create("default", Fields.SCORE, ORDER.desc));
		idSearchConfig.addConfigSet(indexSimpleConfig.getFieldConfigSets().get(IndexParameter.FL));
		idSearchConfig.setDefaultSortName("default");
		addConfiguration(idSearchConfig);
		
		
		// PL_SEARCH Config

		IndexConfiguration plSearchConfig = new SearchByIdConfiguration(Configurations.PL_SEARCH);
		plSearchConfig.addSortConfig(SortConfig.create("default", Fields.SCORE, ORDER.desc));
		plSearchConfig.addConfigSet(indexSimpleConfig.getFieldConfigSets().get(IndexParameter.FL));
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

	
	
	public enum Fields implements IndexField {
		ID("id"),
		PROTEIN_EXISTENCE("protein_existence"),
		PE_LEVEL("pe_level"),			
		PUBLI_CURATED_COUNT("publi_curated_count"),
		PUBLI_LARGE_SCALE_COUNT("publi_large_scale_count"),
		PUBLI_COMPUTED_COUNT("publi_computed_count"),
		INFORMATIONAL_SCORE("informational_score"),
		FILTERS("filters"),
		EC_NAME("ec_name"),
		FUNCTION_DESC("function_desc"),
		CHR_LOC("chr_loc"),
		CHR_LOC_S("chr_loc_s"),
		ISOFORM_NUM("isoform_num"),
		PTM_NUM("ptm_num"),
		VAR_NUM("var_num"),
		AA_LENGTH("aa_length"),
		RECOMMENDED_AC("recommended_ac"),
		RECOMMENDED_NAME("recommended_name"),
		RECOMMENDED_NAME_S("recommended_name_s"),
		UNIPROT_NAME("uniprot_name"),
		ALTERNATIVE_ACS("alternative_acs"),
		ALTERNATIVE_NAMES("alternative_names"),
		RECOMMENDED_GENE_NAMES("recommended_gene_names"),
		RECOMMENDED_GENE_NAMES_S("recommended_gene_names_s"),
		ALTERNATIVE_GENE_NAMES("alternative_gene_names"),
		ORF_NAMES("orf_names"),
		FAMILY_NAMES("family_names"),
		FAMILY_NAMES_S("family_names_s"),
		ANNOTATIONS("annotations"),
		CV_NAMES("cv_names"),
		CV_SYNONYMS("cv_synonyms"),
		CV_ANCESTORS("cv_ancestors"),
		CV_ACS("cv_acs"),
		CV_ANCESTORS_ACS("cv_ancestors_acs"),
		XREFS("xrefs"),
		PUBLICATIONS("publications"),
		CLONE_NAME("clone_name"),
		ENSEMBL("ensembl"),
		MICROARRAY_PROBE("microarray_probe"),
		GENE_BAND("gene_band"),
		PEPTIDE("peptide"),
		ANTIBODY("antibody"),
		INTERACTIONS("interactions"),
		EXPRESSION("expression"),
		TEXT("text"),
		SCORE("score");
		
		private String fieldName;
		  
		private Fields(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getName() {
			return this.fieldName;
		}

		public static String[] stringValues() {
			Fields[] fs = values();
			String[] sv = new String[fs.length];
			for(int i=0; i<sv.length; i++)
				sv[i] = fs[i].fieldName;
				
			return sv;
		}
		
		
	}


}
