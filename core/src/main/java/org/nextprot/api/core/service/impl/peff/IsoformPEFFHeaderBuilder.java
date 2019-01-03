package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformPEFFHeader;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IsoformPEFFHeaderBuilder {

    private final List<Annotation> isoformAnnotations;
    private final Isoform isoform;
    private final Overview overview;
    private final IsoformPEFFHeader peff = new IsoformPEFFHeader();
    private final Function<String, Optional<String>> cvTermToPsiModAccessionFunc;
    private final Function<String, Optional<String>> cvTermToPsiModNameFunc;

    public IsoformPEFFHeaderBuilder(Isoform isoform, List<Annotation> isoformAnnotations, Overview overview,
                                    Function<String, Optional<String>> cvTermToPsiModAccessionFunc,
                                    Function<String, Optional<String>> cvTermToPsiModNameFunc) {

        this.isoform = isoform;
        this.isoformAnnotations = isoformAnnotations;
        this.overview = overview;
        this.cvTermToPsiModAccessionFunc = cvTermToPsiModAccessionFunc;
        this.cvTermToPsiModNameFunc = cvTermToPsiModNameFunc;
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

        // TODO: should be skipped or handled at the data integration level
	    List<Annotation> annots = isoformAnnotations.stream()
			    // remove annotations that are ptms on isoform variant
			    .filter(annotation -> (annotation.getAPICategory() != AnnotationCategory.MODIFIED_RESIDUE) ||
					    !annotation.getDescription().contains("; in variant "))
			    .collect(Collectors.toList());

        peff.setModResPsiFormat(new PEFFModResPsi(isoform.getIsoformAccession(), annots,
                cvTermToPsiModAccessionFunc, cvTermToPsiModNameFunc, unmappedUniprotModAnnotations).format());

        peff.setModResFormat(new PEFFModRes(isoform.getIsoformAccession(), isoformAnnotations,
                unmappedUniprotModAnnotations).format());

        return this;
    }

    IsoformPEFFHeaderBuilder withVariantSimpleFormat() {

        peff.setVariantSimpleFormat(new PEFFVariantSimple(isoform.getIsoformAccession(), isoformAnnotations)
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withVariantComplexFormat() {

        peff.setVariantComplexFormat(new PEFFVariantComplex(isoform.getIsoformAccession(), isoformAnnotations)
                .format());

        return this;
    }

    IsoformPEFFHeaderBuilder withProcessedMoleculeFormat() {

        peff.setProcessedMoleculeFormat(new PEFFProcessedMolecule(isoform.getIsoformAccession(), isoformAnnotations)
                .format());

        return this;
    }

    private String getProteinName() {

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
