package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IsoformSequenceInfoPeff implements Serializable {

    private static final long serialVersionUID = 1L;

    private String headerFormat;

    private String isoformAccession;

    private String isoformAccessionFormat;

    private String proteinNameFormat;

    private String geneNameFormat;

    private String ncbiTaxonomyIdentifierFormat;

    private String taxonomyNameFormat;

    private String sequenceLengthFormat;

    private String sequenceVersionFormat;

    private String entryVersionFormat;

    private String proteinEvidenceFormat;

    private String variantSimpleFormat;

    private String variantComplexFormat;

    private String modResPsiFormat;

    private String modResFormat;

    private String processedMoleculeFormat;

    public String getHeaderFormat() {

        // lazy building
        if (headerFormat == null) {
            this.headerFormat = formatHeader();
        }
        return headerFormat;
    }

    private String formatHeader() {

        String prefixUniqueId = ">nxp:" + getIsoformAccession();

        List<String> keyValuePairsList = Arrays.asList(
                prefixUniqueId,                    // >nxp
                getIsoformAccessionFormat(),       // \DbUniqueId
                getProteinNameFormat(),            // \PName
                getGeneNameFormat(),               // \GName
                getNcbiTaxonomyIdentifierFormat(), // \Ncbi
                getTaxonomyNameFormat(),           // \TaxName
                getSequenceLengthFormat(),         // \Length
                getSequenceVersionFormat(),        // \SV
                getEntryVersionFormat(),           // \EV
                getProteinEvidenceFormat(),        // \PE
                getModResPsiFormat(),              // \ModResPsi
                getModResFormat(),                 // \ModRes
                getVariantSimpleFormat(),          // \VariantSimple
                getVariantComplexFormat(),         // \VariantComplex
                getProcessedMoleculeFormat()       // \Processed
        );

        return keyValuePairsList.stream().collect(Collectors.joining(" "));
    }

    public String getIsoformAccession() {
        return isoformAccession;
    }

    public void setIsoformAccession(String isoformAccession) {
        this.isoformAccession = isoformAccession;
    }

    public String getIsoformAccessionFormat() {
        return isoformAccessionFormat;
    }

    public void setIsoformAccessionFormat(String isoformAccessionFormat) {
        this.isoformAccessionFormat = isoformAccessionFormat;
    }

    public String getProteinNameFormat() {
        return proteinNameFormat;
    }

    public void setProteinNameFormat(String proteinNameFormat) {
        this.proteinNameFormat = proteinNameFormat;
    }

    public String getGeneNameFormat() {
        return geneNameFormat;
    }

    public void setGeneNameFormat(String geneNameFormat) {
        this.geneNameFormat = geneNameFormat;
    }

    public String getNcbiTaxonomyIdentifierFormat() {
        return ncbiTaxonomyIdentifierFormat;
    }

    public void setNcbiTaxonomyIdentifierFormat(String ncbiTaxonomyIdentifierFormat) {
        this.ncbiTaxonomyIdentifierFormat = ncbiTaxonomyIdentifierFormat;
    }

    public String getTaxonomyNameFormat() {
        return taxonomyNameFormat;
    }

    public void setTaxonomyNameFormat(String taxonomyNameFormat) {
        this.taxonomyNameFormat = taxonomyNameFormat;
    }

    public String getSequenceLengthFormat() {
        return sequenceLengthFormat;
    }

    public void setSequenceLengthFormat(String sequenceLengthFormat) {
        this.sequenceLengthFormat = sequenceLengthFormat;
    }

    public String getSequenceVersionFormat() {
        return sequenceVersionFormat;
    }

    public void setSequenceVersionFormat(String sequenceVersionFormat) {
        this.sequenceVersionFormat = sequenceVersionFormat;
    }

    public String getEntryVersionFormat() {
        return entryVersionFormat;
    }

    public void setEntryVersionFormat(String entryVersionFormat) {
        this.entryVersionFormat = entryVersionFormat;
    }

    public String getProteinEvidenceFormat() {
        return proteinEvidenceFormat;
    }

    public void setProteinEvidenceFormat(String proteinEvidenceFormat) {
        this.proteinEvidenceFormat = proteinEvidenceFormat;
    }

    public String getVariantSimpleFormat() {
        return variantSimpleFormat;
    }

    public void setVariantSimpleFormat(String variantSimpleFormat) {
        this.variantSimpleFormat = variantSimpleFormat;
    }

    public String getVariantComplexFormat() {
        return variantComplexFormat;
    }

    public void setVariantComplexFormat(String variantComplexFormat) {
        this.variantComplexFormat = variantComplexFormat;
    }

    public String getModResPsiFormat() {
        return modResPsiFormat;
    }

    public void setModResPsiFormat(String modResPsiFormat) {
        this.modResPsiFormat = modResPsiFormat;
    }

    public String getModResFormat() {
        return modResFormat;
    }

    public void setModResFormat(String modResFormat) {
        this.modResFormat = modResFormat;
    }

    public String getProcessedMoleculeFormat() {
        return processedMoleculeFormat;
    }

    public void setProcessedMoleculeFormat(String processedMoleculeFormat) {
        this.processedMoleculeFormat = processedMoleculeFormat;
    }
}
