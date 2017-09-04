package org.nextprot.api.core.service.impl.peff;

import com.google.common.collect.Sets;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A controlled vocabulary neither Unimod nor PSI-MOD or custom
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModResFormatter extends PTMInfoFormatter {

    private final Map<AnnotationCategory, PTMInfoFormatter> formatterMap;

    public ModResFormatter() {

        super(Sets.union(GlycosylationOrSelenoCysteine.ANNOTATION_CATEGORIES, DisulfideBond.ANNOTATION_CATEGORIES),
                SequenceDescriptorKey.MOD_RES);

        formatterMap = new HashMap<>();

        formatterMap.put(AnnotationCategory.GLYCOSYLATION_SITE, new GlycosylationOrSelenoCysteine());
        formatterMap.put(AnnotationCategory.SELENOCYSTEINE, new GlycosylationOrSelenoCysteine());
        formatterMap.put(AnnotationCategory.DISULFIDE_BOND, new DisulfideBond());
    }

    private PTMInfoFormatter getFormatter(Annotation annotation) {

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
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        getFormatter(annotation).formatAnnotation(isoformAccession, annotation, sb);
    }

    private static class GlycosylationOrSelenoCysteine extends PTMInfoFormatter {

        static final Set<AnnotationCategory> ANNOTATION_CATEGORIES = EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE);

        private GlycosylationOrSelenoCysteine() {

            super(ANNOTATION_CATEGORIES, SequenceDescriptorKey.MOD_RES);
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

    private static class DisulfideBond extends PTMInfoFormatter {

        static final Set<AnnotationCategory> ANNOTATION_CATEGORIES = EnumSet.of(AnnotationCategory.DISULFIDE_BOND);

        private DisulfideBond() {

            super(ANNOTATION_CATEGORIES, SequenceDescriptorKey.MOD_RES);
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
        protected void formatAnnotation(String isoformAccession, Annotation disulfideBondAnnotation, StringBuilder sb) {

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
}