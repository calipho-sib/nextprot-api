package org.nextprot.api.core.domain.release;

/**
 * @author Valentine Rech de Laval
 * @since 2019-11-22
 */
public enum ReleaseStatQueries {
    MASTER("E5UPVHN5"),
    ISOFORM("6SNWGF02"),
    //    IDENTIFIER(""),
    //    INTERACTION(""),
    //    PROTEIN_PTM(""),
    NB_MAPPED_PEPTIDES("J5AADDH7"),
    //    PROTEIN_SEQUENCE_VARIANT(""),
    //    CVTERM(""),
    PUBLI("B2B3MK50"),
    //    MASTER_CVTERM_LINK(""),
    PROTEIN_LEVEL_MASTER("GMKPWQYG"),
    TRANSCRIPT_LEVEL_MASTER("DJX7APYV"),
    HOMOLOGY_MASTER("KZQGU1VB"),
    PREDICTED_MASTER("B3WEFXLJ"),
    UNCERTAIN_MASTER("DAKGCRDA"),
    W_MOLEC_FUNC_MASTER("W8OQVVNJ"),
    //    W_BIO_PROC_MASTER(""),
    W_DISEASE_MASTER("7971GN1P"),
    W_EXPRESSION_MASTER("JASXQ1W1"),
    W_SUBCELL_LOC_MASTER("PL3W9FDJ"),
    //    W_MUTAGENESIS_MASTER(""),
    W_PROTEOMICS_MASTER("LNJJRFO2"),
    W_STRUCT_MASTER("RUFAVK44"),
    CHR_1_MASTER("5UCEVZ6S"),
    CHR_2_MASTER("MYZ2XUNS"),
    CHR_3_MASTER("4DG6Z85J"),
    CHR_4_MASTER("T3OGCXWO"),
    CHR_5_MASTER("AE9NW3K7"),
    CHR_6_MASTER("BLRS11KD"),
    CHR_7_MASTER("TVT8X7AC"),
    CHR_8_MASTER("UO2QH3U9"),
    CHR_9_MASTER("Z4WA818W"),
    CHR_10_MASTER("O0R3PPEN"),
    CHR_11_MASTER("IU07ADCP"),
    CHR_12_MASTER("N82MW93Y"),
    CHR_13_MASTER("4O0Q3PDG"),
    CHR_14_MASTER("3XZDOXDM"),
    CHR_15_MASTER("WZ6Z5HC7"),
    CHR_16_MASTER("KVARIQZR"),
    CHR_17_MASTER("GYXNTS35"),
    CHR_18_MASTER("CQ8ZB21K"),
    CHR_19_MASTER("Y4DAA198"),
    CHR_20_MASTER("WVYG0II5"),
    CHR_21_MASTER("I4QH5GVG"),
    CHR_22_MASTER("VM58NMNQ"),
    CHR_X_MASTER("W5ZS843N"),
    CHR_Y_MASTER("9HXWPJPL"),
    CHR_MT_MASTER("B0D3F4X1"),
    CHR_UNKNOWN_MASTER("WQTUJUIP"),
    //    CITED_PUBLI(""),
    //    COMPUTED_PUBLI(""),
    LARGE_SCALE_PUBLI("9WD26PSY");
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
