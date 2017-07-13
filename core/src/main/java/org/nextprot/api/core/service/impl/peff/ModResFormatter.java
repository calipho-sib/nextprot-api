package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.EnumSet;

/**
 * A controlled vocabulary neither Unimod nor PSI-MOD or custom
 *
 * Created by fnikitin on 05/05/15.
 */
public class ModResFormatter extends PTMInfoFormatter {

    public ModResFormatter() {

        super(EnumSet.of(AnnotationCategory.GLYCOSYLATION_SITE, AnnotationCategory.SELENOCYSTEINE), SequenceDescriptorKey.MOD_RES);
    }

    @Override
    protected String getModAccession(Annotation annotation) {

        return annotation.getCvTermName();
    }
}