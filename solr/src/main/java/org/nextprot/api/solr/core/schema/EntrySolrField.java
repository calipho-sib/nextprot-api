package org.nextprot.api.solr.core.schema;

import org.nextprot.api.solr.core.SolrField;

import java.util.List;

public enum EntrySolrField implements SolrField {
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
    CHR_LOC_S("chr_loc_s", Integer.class),
    ISOFORM_NUM("isoform_num", Integer.class),
    PTM_NUM("ptm_num", Integer.class),
    VAR_NUM("var_num", Integer.class),
    AA_LENGTH("aa_length", Integer.class),
    IDSP0("idsp0", "idsp0", String.class), // a copy of ID but having type text_split0
    RECOMMENDED_AC("recommended_ac","ac", String.class),
    RECOMMENDED_NAME("recommended_name", String.class),
    RECOMMENDED_NAME_S("recommended_name_s", String.class),
    CD_ANTIGEN("cd_antigen", List.class),
    INTERNATIONAL_NAME("international_name", List.class),
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
    private Class<?> fieldType;

    EntrySolrField(String fieldName, Class<?> fieldType) {
        this(fieldName, null, fieldType);
    }

    EntrySolrField(String fieldName, String publicName, Class<?> fieldType) {
        this.fieldName = fieldName;
        this.publicName = publicName;
        this.fieldType = fieldType;
    }

    public String getName() {
        return this.fieldName;
    }

    @Override
    public String getPublicName() {
        return this.publicName;
    }

    @Override
    public boolean hasPublicName() {
        return this.publicName!=null && this.publicName.length()>0;
    }

    public Class<?> getType() {
        return fieldType;
    }
}
