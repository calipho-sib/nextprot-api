package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;

/**
 * A part of PEFF header description composed of a key and a value
 */
public abstract class PEFFInformation {

    private final Key key;

    PEFFInformation(Key key) {

        Preconditions.checkNotNull(key);
        this.key = key;
    }

    /**
     * Format the PEFF info value or null or empty if not defined
     */
    protected abstract String formatValue();

    public String format() {

        String value = formatValue();

        if (value != null && !value.isEmpty()) {
            return "\\" + key.getName() + "=" + value;
        }

        return "";
    }

    public enum Key {

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

        Key(String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }
    }
}
