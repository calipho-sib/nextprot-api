package org.nextprot.api.core.service.impl.peff;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A controlled vocabulary neither Unimod nor PSI-MOD or custom
 *
 * Created by fnikitin on 05/05/15.
 */
public class PEFFModRes extends PEFFPTMInformation {

    private final List<PEFFPTMInformation> formatterList;
    private final List<Annotation> unmappedUniprotModAnnotations;

    PEFFModRes(String isoformAccession, List<Annotation> isoformAnnotations, List<Annotation> unmappedUniprotModAnnotations) {

        super(isoformAccession, isoformAnnotations, Sets.union(PEFFGlycosylationOrSelenoCysteine.ANNOTATION_CATEGORIES, PEFFDisulfideBond.ANNOTATION_CATEGORIES),
                Key.MOD_RES);

        formatterList = new ArrayList<>();

        formatterList.add(new PEFFGlycosylationOrSelenoCysteine(isoformAccession, isoformAnnotations));
        formatterList.add(new PEFFDisulfideBond(isoformAccession, isoformAnnotations));
        formatterList.add(new PEFFNonMappingModResPsi(isoformAccession, isoformAnnotations));

        this.unmappedUniprotModAnnotations = unmappedUniprotModAnnotations;
    }

    private PEFFPTMInformation getPEFFPTMInformation(Annotation annotation) {

        for (PEFFPTMInformation information : formatterList) {

            if (information.selectAnnotation(annotation)) {
                return information;
            }
        }

        throw new IllegalStateException("Could not handle format in PEFF "+annotation.getAPICategory());
    }

    @Override
    protected String getModAccession(Annotation annotation) {

        return getPEFFPTMInformation(annotation).getModAccession(annotation);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return getPEFFPTMInformation(annotation).getModName(annotation);
    }

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        getPEFFPTMInformation(annotation).formatAnnotation(annotation, sb);
    }

    @Override
    protected List<Annotation> filterAnnotation(String isoformAccession) {

        List<Annotation> selectedAnnotations = super.filterAnnotation(isoformAccession);

        selectedAnnotations.addAll(unmappedUniprotModAnnotations);

        return selectedAnnotations;
    }

    private static class PEFFGlycosylationOrSelenoCysteine extends PEFFPTMInformation {

        static final Set<AnnotationCategory> ANNOTATION_CATEGORIES = EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE);

        private PEFFGlycosylationOrSelenoCysteine(String isoformAccession, List<Annotation> isoformAnnotations) {

            super(isoformAccession, isoformAnnotations, ANNOTATION_CATEGORIES, Key.MOD_RES);
        }

        @Override
        protected String getModAccession(Annotation annotation) {

            return "";
        }

        @Override
        protected String getModName(Annotation annotation) {

            return annotation.getCvTermName();
        }
    }

    private static class PEFFDisulfideBond extends PEFFPTMInformation {

        static final Set<AnnotationCategory> ANNOTATION_CATEGORIES = EnumSet.of(AnnotationCategory.DISULFIDE_BOND);

        private PEFFDisulfideBond(String isoformAccession, List<Annotation> isoformAnnotations) {

            super(isoformAccession, isoformAnnotations, ANNOTATION_CATEGORIES, Key.MOD_RES);
        }

        @Override
        protected final String getModAccession(Annotation annotation) {

            return "";
        }

        @Override
        protected String getModName(Annotation annotation) {

            return "Disulfide";
        }


        // \ModRes=(28||O-linked (GalNAc...))(49||Disulfide)(85||Disulfide)(84||Disulfide)(97||Disulfide)(473||Disulfide)(478||Disulfide)(74||Disulfide)(339||Disulfide)(214||Disulfide)(317||Disulfide)
        @Override
        protected void formatAnnotation(Annotation disulfideBondAnnotation, StringBuilder sb) {

            Integer startPos = disulfideBondAnnotation.getStartPositionForIsoform(isoformAccession);
            Integer endPos = disulfideBondAnnotation.getEndPositionForIsoform(isoformAccession);

            sb
                    .append("(")
                    .append((startPos != null) ? startPos : "?")
                    .append("||")
                    .append("Disulfide")
                    .append(")")
                    .append("(")
                    .append((endPos != null) ? endPos : "?")
                    .append("||")
                    .append("Disulfide")
                    .append(")")
            ;
        }
    }

    private static class PEFFNonMappingModResPsi extends PEFFPTMInformation {

        PEFFNonMappingModResPsi(String isoformAccession, List<Annotation> isoformAnnotations) {

            super(isoformAccession, isoformAnnotations, EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE,
                    AnnotationCategory.CROSS_LINK, AnnotationCategory.LIPIDATION_SITE), Key.MOD_RES);
        }

        @Override
        protected String getModAccession(Annotation annotation) {
            return "";
        }

        @Override
        protected String getModName(Annotation annotation) {
            return annotation.getCvTermName();
        }

        @Override
        protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

            sb
                    .append("(")
                    .append(annotation.getStartPositionForIsoform(isoformAccession))
                    .append("||")
                    .append(getModName(annotation))
                    .append(")")
            ;
        }
    }
}