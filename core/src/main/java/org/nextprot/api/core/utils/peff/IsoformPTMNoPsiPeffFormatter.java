package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;
import java.util.Set;

/**
 * A Modified residue without PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformPTMNoPsiPeffFormatter extends IsoformPTMPeffFormatter {

    private static final Set<AnnotationApiModel> SUPPORTED_MODELS = EnumSet.of(AnnotationApiModel.GLYCOSYLATION_SITE, AnnotationApiModel.SELENOCYSTEINE);

    IsoformPTMNoPsiPeffFormatter(String isoformId, Annotation annotation) {

        super(isoformId, annotation, SUPPORTED_MODELS, annotation.getCvTermName());
    }

    @Override
    public boolean isPSI() {
        return false;
    }

    public static boolean isModelSupported(AnnotationApiModel model) {

        return SUPPORTED_MODELS.contains(model);
    }
}