package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.TerminologyService;

import java.util.ArrayList;
import java.util.List;

public class IsoformPEFFHeaderBuilder {

    private final Entry entry;
    private final Isoform isoform;
    private final Overview overview;
    private final IsoformPEFFHeader peff = new IsoformPEFFHeader();
    private final TerminologyService terminologyService;

    public IsoformPEFFHeaderBuilder(String isoformAccession, Entry entry, IsoformService isoformService, TerminologyService terminologyService) {

        this.entry = entry;
        isoform = isoformService.findIsoformByName(entry.getUniqueName(), isoformAccession);
        overview = entry.getOverview();

        this.terminologyService = terminologyService;
    }

    public IsoformPEFFHeaderBuilder withEverything() {

        return withIsoformAccession()
                .withIsoformAccessionFormat()
                .withProteinNameFormat()
                .withGeneNameFormat()
                .withNcbiTaxonomyIdentifierFormat()
                .withTaxonomyNameFormat()
                .withSequenceLengthFormat()
                .withSequenceVersionFormat()
                .withEntryVersionFormat()
                .withProteinEvidenceFormat()
                .withModResFormats()
                .withVariantSimpleFormat()
                .withVariantComplexFormat()
                .withProcessedMoleculeFormat();
    }

    IsoformPEFFHeaderBuilder withIsoformAccession() {

        peff.setIsoformAccession(isoform.getIsoformAccession());
        return this;
    }

    IsoformPEFFHeaderBuilder withIsoformAccessionFormat() {

        peff.setIsoformAccessionFormat(
                new SimplePEFFInformation(PEFFInformation.Key.DB_UNIQUE_ID, isoform.getIsoformAccession())
                        .format()
        );
        return this;
    }

    IsoformPEFFHeaderBuilder withProteinNameFormat() {

        peff.setProteinNameFormat(
                new SimplePEFFInformation(PEFFInformation.Key.P_NAME, getProteinName())
                    .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withGeneNameFormat() {

        peff.setGeneNameFormat(new SimplePEFFInformation(PEFFInformation.Key.G_NAME, getGeneName())
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withNcbiTaxonomyIdentifierFormat() {

        peff.setNcbiTaxonomyIdentifierFormat(new SimplePEFFInformation(PEFFInformation.Key.NCBI_TAX_ID,
                String.valueOf(getNcbiTaxonomyIdentifier())).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withTaxonomyNameFormat() {

        peff.setTaxonomyNameFormat(new SimplePEFFInformation(PEFFInformation.Key.TAX_NAME, getTaxonomyName())
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withSequenceLengthFormat() {

        peff.setSequenceLengthFormat(new SimplePEFFInformation(PEFFInformation.Key.LENGTH,
                String.valueOf(isoform.getSequenceLength())).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withSequenceVersionFormat() {

        peff.setSequenceVersionFormat(new SimplePEFFInformation(PEFFInformation.Key.SV,
                overview.getHistory().getSequenceVersion()).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withEntryVersionFormat() {

        peff.setEntryVersionFormat(new SimplePEFFInformation(PEFFInformation.Key.EV,
                overview.getHistory().getUniprotVersion()).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withProteinEvidenceFormat() {

        peff.setProteinEvidenceFormat(new SimplePEFFInformation(PEFFInformation.Key.PE,
                String.valueOf(overview.getProteinExistence().getLevel())).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withModResFormats() {

        List<Annotation> unmappedUniprotModAnnotations = new ArrayList<>();

        peff.setModResPsiFormat(new PEFFModResPsi(entry, isoform.getIsoformAccession(),
                terminologyService::findPsiModAccession,
                terminologyService::findPsiModName,
                unmappedUniprotModAnnotations
                ).format()
        );

        peff.setModResFormat(new PEFFModRes(entry, isoform.getIsoformAccession(), unmappedUniprotModAnnotations)
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withVariantSimpleFormat() {

        peff.setVariantSimpleFormat(new PEFFVariantSimple(entry, isoform.getIsoformAccession())
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withVariantComplexFormat() {

        peff.setVariantComplexFormat(new PEFFVariantComplex(entry, isoform.getIsoformAccession())
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withProcessedMoleculeFormat() {

        peff.setProcessedMoleculeFormat(new PEFFProcessedMolecule(entry, isoform.getIsoformAccession())
                .format());

        return this;
    }

    private String getProteinName() {

        Overview overview = entry.getOverview();

        return (overview.hasMainProteinName()) ?
                overview.getMainProteinName() + " isoform " + isoform.getMainEntityName().getName() : "";
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

    public IsoformPEFFHeader build() {

        return peff;
    }
}
