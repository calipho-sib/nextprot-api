package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformSequenceInfoPeff;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.impl.peff.*;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeffServiceImpl implements PeffService {

    @Autowired
    private IsoformService isoformService;

    @Autowired
    private OverviewService overviewService;

    @Autowired
    private EntryService entryService;

    @Autowired
    private TerminologyService terminologyService;

    @Override
    public IsoformSequenceInfoPeff formatSequenceInfo(String isoformAccession) {
        
        IsoformSequenceInfoPeff peff = new IsoformSequenceInfoPeff();

        peff.setIsoformAccession(isoformAccession);
        peff.setIsoformAccessionFormat(formatIsoformAccession(isoformAccession));
        peff.setProteinNameFormat(formatProteinName(isoformAccession));
        peff.setGeneNameFormat(formatGeneName(isoformAccession));
        peff.setNcbiTaxonomyIdentifierFormat(formatNcbiTaxonomyIdentifier(isoformAccession));
        peff.setTaxonomyNameFormat(formatTaxonomyName(isoformAccession));
        peff.setSequenceLengthFormat(formatSequenceLength(isoformAccession));
        peff.setSequenceVersionFormat(formatSequenceVersion(isoformAccession));
        peff.setEntryVersionFormat(formatEntryVersion(isoformAccession));
        peff.setProteinEvidenceFormat(formatProteinEvidence(isoformAccession));
        peff.setModResPsiFormat(formatModResPsi(isoformAccession));
        peff.setModResFormat(formatModRes(isoformAccession));
        peff.setVariantSimpleFormat(formatVariantSimple(isoformAccession));
        peff.setVariantComplexFormat(formatVariantComplex(isoformAccession));
        peff.setProcessedMoleculeFormat(formatProcessedMolecule(isoformAccession));

        return peff;
    }

    // TODO: Refactor this code to traverse Entry once, gather all and then give them to proper formatter

    @Override
    public String formatIsoformAccession(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.DB_UNIQUE_ID, isoformAccession)
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatProteinName(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.P_NAME, getProteinName(isoformAccession))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatGeneName(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.G_NAME, getGeneName(isoformAccession))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatNcbiTaxonomyIdentifier(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.NCBI_TAX_ID, String.valueOf(getNcbiTaxonomyIdentifier()))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatTaxonomyName(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.TAX_NAME, getTaxonomyName())
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatSequenceLength(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.LENGTH, String.valueOf(getSequenceLength(isoformAccession)))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatSequenceVersion(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.SV, getSequenceVersion(isoformAccession))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatEntryVersion(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.EV, getEntryVersion(isoformAccession))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatProteinEvidence(String isoformAccession) {

        return new SimpleSequenceInfoFormatter(SequenceDescriptorKey.PE, String.valueOf(getProteinEvidence(isoformAccession)))
                .format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatVariantSimple(String isoformAccession) {
        return new VariantSimpleFormatter().format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatVariantComplex(String isoformAccession) {
        return new VariantComplexFormatter().format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatModResPsi(String isoformAccession) {

        return new ModResPsiFormatter(
                (cvTerm) -> terminologyService.findPsiModAccession(cvTerm),
                (cvTerm) -> terminologyService.findPsiModName(cvTerm)
        ).format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatModRes(String isoformAccession) {

        return new ModResFormatter().format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    @Override
    public String formatProcessedMolecule(String isoformAccession) {

        return new ProcessedMoleculeFormatter().format(entryService.findEntryFromIsoformAccession(isoformAccession), isoformAccession);
    }

    private String getProteinName(String isoformAccession) {

        Isoform isoform = isoformService.findIsoformByAccession(isoformAccession);
        Overview overview = overviewService.findOverviewByEntry(getEntryAccession(isoformAccession));

        return (overview.hasMainProteinName()) ? overview.getMainProteinName() + " isoform " + isoform.getMainEntityName().getName() : "";
    }

    private String getGeneName(String isoformAccession) {

        Isoform isoform = isoformService.findIsoformByAccession(isoformAccession);
        Overview overview = overviewService.findOverviewByEntry(getEntryAccession(isoformAccession));

        return (overview.hasMainGeneName()) ? overview.getMainGeneName() : "";
    }

    private int getNcbiTaxonomyIdentifier() {

        return 9606;
    }

    private String getTaxonomyName() {

        return "Homo Sapiens";
    }

    private int getSequenceLength(String isoformAccession) {

        return isoformService.findIsoformByAccession(isoformAccession).getSequenceLength();
    }

    private String getSequenceVersion(String isoformAccession) {

        Overview overview = overviewService.findOverviewByEntry(getEntryAccession(isoformAccession));

        return overview.getHistory().getSequenceVersion();
    }

    private String getEntryVersion(String isoformAccession) {

        Overview overview = overviewService.findOverviewByEntry(getEntryAccession(isoformAccession));

        return overview.getHistory().getUniprotVersion();
    }

    private int getProteinEvidence(String isoformAccession) {

        Overview overview = overviewService.findOverviewByEntry(getEntryAccession(isoformAccession));

        return overview.getProteinExistenceLevel();
    }

    private String getEntryAccession(String isoformAccession) {

        return entryService.findEntryFromIsoformAccession(isoformAccession).getUniqueName();
    }
}
