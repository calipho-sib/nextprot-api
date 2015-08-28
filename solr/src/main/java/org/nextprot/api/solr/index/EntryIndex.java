package org.nextprot.api.solr.index;

import java.util.List;

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
		public static final String AUTOCOMPLETE = "autocomplete";
		public static final String ID_SEARCH = "id";
		public static final String PL_SEARCH = "pl_search";
	}

	@Override
	protected void setupConfigurations() {
		
		// SIMPLE Config
		
		IndexConfiguration indexConfig = new IndexConfiguration(Configurations.SIMPLE);
		
		indexConfig.addConfigSet(FieldConfigSet.create(IndexParameter.FL)
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

		indexConfig.addConfigSet(FieldConfigSet.create(IndexParameter.QF)
			//.add(Fields.ID,64)
			.add(Fields.IDSP0,64)
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
		
		indexConfig.addConfigSet(FieldConfigSet.create(IndexParameter.PF)
			//.add(Fields.ID, 640)
			.add(Fields.IDSP0,640)
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
				SortConfig.create("gene", Fields.RECOMMENDED_GENE_NAMES_S, ORDER.asc),
				SortConfig.create("protein", Fields.RECOMMENDED_NAME_S, ORDER.asc),
				SortConfig.create("family", Fields.FAMILY_NAMES_S, ORDER.asc),
				SortConfig.create("length", Fields.AA_LENGTH, ORDER.asc),
				SortConfig.create("ac", Fields.ID, ORDER.asc),
				SortConfig.create("chromosome", Fields.CHR_LOC_S, ORDER.asc),
				SortConfig.create("default", Fields.SCORE, ORDER.desc, 100)
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
		idSearchConfig.addSortConfig(SortConfig.create("default", Fields.SCORE, ORDER.desc));
		idSearchConfig.addConfigSet(indexConfig.getFieldConfigSets().get(IndexParameter.FL));
		idSearchConfig.setDefaultSortName("default");
		addConfiguration(idSearchConfig);
		
		
		// PL_SEARCH Config

		IndexConfiguration plSearchConfig = new SearchByIdConfiguration(Configurations.PL_SEARCH);
		plSearchConfig.addSortConfig(SortConfig.create("default", Fields.SCORE, ORDER.desc));
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
		return Fields.values();
	}
	
	
	public enum Fields implements IndexField {
		ID("id","id", String.class), // public name for id is necessary for executeIdQuery() otherwise the id: of the query string is escaped
		PROTEIN_EXISTENCE("protein_existence", String.class),
		PE_LEVEL("pe_level","pe", Integer.class),			
		PUBLI_CURATED_COUNT("publi_curated_count", Integer.class),
		PUBLI_LARGE_SCALE_COUNT("publi_large_scale_count", Integer.class),
		PUBLI_COMPUTED_COUNT("publi_computed_count", Integer.class),
		INFORMATIONAL_SCORE("informational_score", Float.class),
		FILTERS("filters", String.class),
		EC_NAME("ec_name", String.class),
		FUNCTION_DESC("function_desc", List.class), //List<String>
		CHR_LOC("chr_loc", String.class),
		CHR_LOC_S("chr_loc_s", Long.class),
		ISOFORM_NUM("isoform_num", Integer.class),
		PTM_NUM("ptm_num", Integer.class),
		VAR_NUM("var_num", Integer.class),
		AA_LENGTH("aa_length", Integer.class),
		IDSP0("idsp0", "idsp0", String.class), // a copy of ID but having type text_split0
		RECOMMENDED_AC("recommended_ac","ac", String.class),
		RECOMMENDED_NAME("recommended_name", String.class),
		RECOMMENDED_NAME_S("recommended_name_s", String.class), 
		UNIPROT_NAME("uniprot_name", List.class), //Why is it defined as an array???
		ALTERNATIVE_ACS("alternative_acs", List.class),
		ALTERNATIVE_NAMES("alternative_names", List.class),
		RECOMMENDED_GENE_NAMES("recommended_gene_names", String.class),
		RECOMMENDED_GENE_NAMES_S("recommended_gene_names_s", String.class),
		ALTERNATIVE_GENE_NAMES("alternative_gene_names", List.class),
		ORF_NAMES("orf_names", List.class),
		REGION_NAME("region_name", List.class),
		FAMILY_NAMES("family_names", String.class),
		FAMILY_NAMES_S("family_names_s", String.class),
		ANNOTATIONS("annotations", List.class),
		CV_NAMES("cv_names", List.class),
		CV_SYNONYMS("cv_synonyms", List.class),
		CV_ANCESTORS("cv_ancestors", List.class),
		CV_ACS("cv_acs", List.class),
		CV_ANCESTORS_ACS("cv_ancestors_acs", List.class),
		XREFS("xrefs", List.class),
		PUBLICATIONS("publications", List.class),
		CLONE_NAME("clone_name", List.class),
		ENSEMBL("ensembl", List.class),
		MICROARRAY_PROBE("microarray_probe", List.class),
		GENE_BAND("gene_band", List.class),
		PEPTIDE("peptide", List.class),
		ANTIBODY("antibody", List.class),
		INTERACTIONS("interactions", List.class),
		EXPRESSION("expression", List.class),
		//Special fields computed by SOLR
		TEXT("text", List.class),
		SCORE("score", Float.class);
		
		private String fieldName;
		private String publicName;
		private Class<?> clazz;
		
		private Fields(String fieldName) {
			this.fieldName = fieldName;
		}
		private Fields(String fieldName, String publicName) {
			this.fieldName = fieldName;
			this.publicName=publicName;
		}
		
		private Fields(String fieldName, Class<?> clazz) {
			this.fieldName = fieldName;
			this.clazz=clazz;
		}
		private Fields(String fieldName, String publicName, Class<?> clazz) {
			this.fieldName = fieldName;
			this.publicName=publicName;
			this.clazz=clazz;
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

		@Override
		public String getPublicName() {
			return this.publicName;
		}

		@Override
		public boolean hasPublicName() {
			return this.publicName!=null && this.publicName.length()>0;
		}
		public Class<?> getClazz() {
			return this.clazz;
		}
		
		
	}





}
