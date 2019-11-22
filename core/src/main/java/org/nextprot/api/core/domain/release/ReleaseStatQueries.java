package org.nextprot.api.core.domain.release;

/**
 * @author Valentine Rech de Laval
 * @since 2019-11-22
 */
public enum ReleaseStatQueries {
    //    MASTER(""),
    //    ISOFORM(""),
    //    IDENTIFIER(""),
    //    INTERACTION(""),
    //    PROTEIN_PTM(""),
    //    NB_MAPPED_PEPTIDES(""),
    //    PROTEIN_SEQUENCE_VARIANT(""),
    //    CVTERM(""),
    //    PUBLI(""),
    //    MASTER_CVTERM_LINK(""),
    //    PROTEIN_LEVEL_MASTER(""),
    TRANSCRIPT_LEVEL_MASTER("NXQ_00068");
    //    HOMOLOGY_MASTER(""),
    //    PREDICTED_MASTER(""),
    //    UNCERTAIN_MASTER(""),
    //    W_MOLEC_FUNC_MASTER(""),
    //    W_BIO_PROC_MASTER(""),
    //    W_DISEASE_MASTER(""),
    //    W_EXPRESSION_MASTER(""),
    //    W_SUBCELL_LOC_MASTER(""),
    //    W_MUTAGENESIS_MASTER(""),
    //    W_PROTEOMICS_MASTER(""),
    //    W_STRUCT_MASTER(""),
    //    CHR_1_MASTER(""),
    //    CHR_2_MASTER(""),
    //    CHR_3_MASTER(""),
    //    CHR_4_MASTER(""),
    //    CHR_5_MASTER(""),
    //    CHR_6_MASTER(""),
    //    CHR_7_MASTER(""),
    //    CHR_8_MASTER(""),
    //    CHR_9_MASTER(""),
    //    CHR_10_MASTER(""),
    //    CHR_11_MASTER(""),
    //    CHR_12_MASTER(""),
    //    CHR_13_MASTER(""),
    //    CHR_14_MASTER(""),
    //    CHR_15_MASTER(""),
    //    CHR_16_MASTER(""),
    //    CHR_17_MASTER(""),
    //    CHR_18_MASTER(""),
    //    CHR_19_MASTER(""),
    //    CHR_20_MASTER(""),
    //    CHR_21_MASTER(""),
    //    CHR_22_MASTER(""),
    //    CHR_X_MASTER(""),
    //    CHR_Y_MASTER(""),
    //    CHR_MT_MASTER(""),
    //    CHR_UNKNOWN_MASTER(""),
    //    CITED_PUBLI(""),
    //    COMPUTED_PUBLI(""),
    //    LARGE_SCALE_PUBLI(""),
    //    CURATED_PUBLI("");

    private final String queryId;

    ReleaseStatQueries(String queryId) {
        this.queryId = queryId;
    }

    public String getTag() {
        return this.toString();
    }

    public String getQueryId() {
        return queryId;
    }
}
