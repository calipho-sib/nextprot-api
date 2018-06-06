package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;
import java.util.Set;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class PEFFPTMInformation extends AnnotationBasedPEFFInformation {

    PEFFPTMInformation(String isoformAccession, List<Annotation> isoformAnnotations,
                       Set<AnnotationCategory> supportedApiModel,
                       Key Key) {

        super(isoformAccession, isoformAnnotations, supportedApiModel, Key);
    }

    protected abstract String getModAccession(Annotation annotation);
    protected abstract String getModName(Annotation annotation);

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(getModAccession(annotation))
                .append("|")
                .append(getModName(annotation))
                .append(")")
        ;
    }
}
