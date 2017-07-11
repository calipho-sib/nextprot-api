package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.Overview;

public class PeffHeaderFormatterImpl extends PeffHeaderFormatter {

    private final Entry entry;
    private final Isoform isoform;
    private final Overview overview;

    public PeffHeaderFormatterImpl(Entry entry, Isoform isoform) {

        this.entry = entry;
        this.overview = entry.getOverview();
        this.isoform = isoform;
    }

    @Override
    protected String formatIsoformAccession() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.DB_UNIQUE_ID, getIsoformAccession())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatProteinName() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.P_NAME, getProteinName())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatGeneName() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.G_NAME, getGeneName())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatNcbiTaxonomyIdentifier() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.NCBI_TAX_ID, String.valueOf(getNcbiTaxonomyIdentifier()))
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatTaxonomyName() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.TAX_NAME, getTaxonomyName())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatSequenceLength() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.LENGTH, String.valueOf(getSequenceLength()))
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatSequenceVersion() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.SV, getSequenceVersion())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatEntryVersion() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.EV, getEntryVersion())
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatProteinEvidence() {
        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.PE, String.valueOf(getProteinEvidence()))
                .format(entry, getIsoformAccession());
    }

    @Override
    protected String formatVariantSimple() {
        return new VariantSimpleFormatter().format(entry, getIsoformAccession());
    }

    @Override
    protected String formatVariantComplex() {
        return new VariantComplexFormatter().format(entry, getIsoformAccession());
    }

    @Override
    protected String formatModResUnimodFormatter() {
        return new ModResUnimodFormatter().format(entry, getIsoformAccession());
    }

    @Override
    protected String formatModResFormatter() {
        return new ModResFormatter().format(entry, getIsoformAccession());
    }

    @Override
    protected String formatProcessedMoleculeFormatter() {
        return new ProcessedMoleculeFormatter().format(entry, getIsoformAccession());
    }

    @Override
    protected String getIsoformAccession() {

        return isoform.getIsoformAccession();
    }

    private String getProteinName() {

        return (overview.hasMainProteinName()) ? overview.getMainProteinName() : "";
    }

    private String getGeneName() {

        return (overview.hasMainGeneName()) ? overview.getMainGeneName() : "";
    }

    private int getNcbiTaxonomyIdentifier() {

        return 9606;
    }

    private String getTaxonomyName() {

        return "Homo Sapiens";
    }

    private int getSequenceLength() {

        return isoform.getSequenceLength();
    }

    private String getSequenceVersion() {

        return overview.getHistory().getSequenceVersion();
    }

    private String getEntryVersion() {

        return overview.getHistory().getUniprotVersion();
    }

    private int getProteinEvidence() {

        return overview.getProteinExistenceLevel();
    }
}
