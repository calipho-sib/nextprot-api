package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A Modified residue with PSI-MOD identifier
 *
 * Created by fnikitin on 05/05/15.
 */
class ModResUnimodFormatter extends PTMInfoFormatter {

    public ModResUnimodFormatter() {

        super(EnumSet.of(AnnotationCategory.MODIFIED_RESIDUE, AnnotationCategory.CROSS_LINK, AnnotationCategory.LIPIDATION_SITE), SequenceDescriptorKey.MOD_RES_UNIMOD);
    }

    @Override
    protected String getModName(Annotation annotation) {

        return annotation.getCvTermAccessionCode();
    }
}