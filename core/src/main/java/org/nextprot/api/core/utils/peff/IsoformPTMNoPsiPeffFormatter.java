package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A Modified residue without PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformPTMNoPsiPeffFormatter extends IsoformPTMPeffFormatter {

    IsoformPTMNoPsiPeffFormatter() {

        super(EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE), PeffKey.MOD_RES);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return annotation.getCvTermName();
    }
}