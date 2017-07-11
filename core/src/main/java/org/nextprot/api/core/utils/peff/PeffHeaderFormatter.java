package org.nextprot.api.core.utils.peff;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PeffHeaderFormatter {

    public String format() {

        String prefixUniqueId = ">nxp:" + getIsoformAccession();

        List<String> keyValuePairsList = Arrays.asList(
                prefixUniqueId,                    // >nxp
                formatIsoformAccession(),          // \DbUniqueId
                formatProteinName(),               // \PName
                formatGeneName(),                  // \GName
                formatNcbiTaxonomyIdentifier(),    // \Ncbi
                formatTaxonomyName(),              // \TaxName
                formatSequenceLength(),            // \Length
                formatSequenceVersion(),           // \SV
                formatEntryVersion(),              // \EV
                formatProteinEvidence(),           // \PE
                formatModResUnimodFormatter(),     // \ModResUnimod
                formatModResFormatter(),           // \ModRes
                formatVariantSimple(),             // \VariantSimple
                formatVariantComplex(),            // \VariantComplex
                formatProcessedMoleculeFormatter() // \Processed
        );

        return keyValuePairsList.stream().collect(Collectors.joining(" "));
    }

    protected abstract String getIsoformAccession();

    protected abstract String formatIsoformAccession();

    protected abstract String formatProteinName();

    protected abstract String formatGeneName();

    protected abstract String formatNcbiTaxonomyIdentifier();

    protected abstract String formatTaxonomyName();

    protected abstract String formatSequenceLength();

    protected abstract String formatSequenceVersion();

    protected abstract String formatEntryVersion();

    protected abstract String formatProteinEvidence();

    protected abstract String formatVariantSimple();

    protected abstract String formatVariantComplex();

    protected abstract String formatModResUnimodFormatter();

    protected abstract String formatModResFormatter();

    protected abstract String formatProcessedMoleculeFormatter();
}
