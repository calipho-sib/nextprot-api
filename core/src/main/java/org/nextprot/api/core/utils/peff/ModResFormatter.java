package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A controlled vocabulary neither Unimod nor PSI-MOD or custom
 *
 * Created by fnikitin on 05/05/15.
 */
class ModResFormatter extends PTMInfoFormatter {

    ModResFormatter() {

        super(EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE), SequenceDescriptorKey.MOD_RES);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return annotation.getCvTermName();
    }
}