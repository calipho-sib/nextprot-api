package org.nextprot.api.core.service.impl.peff;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * A controlled vocabulary neither Unimod nor PSI-MOD or custom
 *
 * Created by fnikitin on 05/05/15.
 */
public class PEFFModRes extends PEFFPTMInformation {

    private final Map<AnnotationCategory, PEFFPTMInformation> formatterMap;
    private final List<Annotation> unmappedUniprotModAnnotations;

    public PEFFModRes(Entry entry, String isoformAccession, List<Annotation> unmappedUniprotModAnnotations) {

        super(entry, isoformAccession, Sets.union(PEFFGlycosylationOrSelenoCysteine.ANNOTATION_CATEGORIES, PEFFDisulfideBond.ANNOTATION_CATEGORIES),
                Key.MOD_RES);

        formatterMap = new HashMap<>();

        formatterMap.put(AnnotationCategory.GLYCOSYLATION_SITE, new PEFFGlycosylationOrSelenoCysteine(entry, isoformAccession));
        formatterMap.put(AnnotationCategory.SELENOCYSTEINE, new PEFFGlycosylationOrSelenoCysteine(entry, isoformAccession));
        formatterMap.put(AnnotationCategory.DISULFIDE_BOND, new PEFFDisulfideBond(entry, isoformAccession));
        formatterMap.put(AnnotationCategory.MODIFIED_RESIDUE, new PEFFNonMappingModResPsi(entry, isoformAccession));

        this.unmappedUniprotModAnnotations = unmappedUniprotModAnnotations;
    }

    private PEFFPTMInformation getFormatter(Annotation annotation) {

        if (!formatterMap.containsKey(annotation.getAPICategory()))
            throw new IllegalStateException("ModResFormatter does not format "+annotation.getAPICategory());

        return formatterMap.get(annotation.getAPICategory());
    }

    @Override
    protected String getModAccession(Annotation annotation) {

        return getFormatter(annotation).getModAccession(annotation);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return getFormatter(annotation).getModName(annotation);
    }

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        getFormatter(annotation).formatAnnotation(annotation, sb);
    }

    @Override
    protected List<Annotation> selectAnnotation() {

        List<Annotation> selectedAnnotations = super.selectAnnotation();

        selectedAnnotations.addAll(unmappedUniprotModAnnotations);

        return selectedAnnotations;
    }

    private static class PEFFGlycosylationOrSelenoCysteine extends PEFFPTMInformation {

        static final Set<AnnotationCategory> ANNOTATION_CATEGORIES = EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE);

        private PEFFGlycosylationOrSelenoCysteine(Entry entry, String isoformAccession) {

            super(entry, isoformAccession, ANNOTATION_CATEGORIES, Key.MOD_RES);
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

        private PEFFDisulfideBond(Entry entry, String isoformAccession) {

            super(entry, isoformAccession, ANNOTATION_CATEGORIES, Key.MOD_RES);
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

            sb
                    .append("(")
                    .append(disulfideBondAnnotation.getStartPositionForIsoform(isoformAccession))
                    .append("||")
                    .append("Disulfide")
                    .append(")")
                    .append("(")
                    .append(disulfideBondAnnotation.getEndPositionForIsoform(isoformAccession))
                    .append("||")
                    .append("Disulfide")
                    .append(")")
            ;
        }
    }

    private static class PEFFNonMappingModResPsi extends PEFFPTMInformation {

        PEFFNonMappingModResPsi(Entry entry, String isoformAccession) {

            super(entry, isoformAccession, EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE), Key.MOD_RES);
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