package org.nextprot.api.core.utils.peff;

/**
 *
 * Created by fnikitin on 11.07.17.
 */
public enum SequenceDescriptorKey {

    DB_UNIQUE_ID("DbUniqueId"),
    P_NAME("PName"),
    G_NAME("GName"),
    NCBI_TAX_ID("NcbiTaxId"),
    TAX_NAME("TaxName"),
    LENGTH("Length"),
    SV("SV"),
    EV("EV"),
    PE("PE"),
    MOD_RES_PSI("ModResPsi"),
    MOD_RES_UNIMOD("ModResUnimod"),
    MOD_RES("ModRes"),
    VARIANT_SIMPLE("VariantSimple"),
    VARIANT_COMPLEX("VariantComplex"),
    PROCESSED("Processed")
    ;

    String name;

    SequenceDescriptorKey(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }
}
