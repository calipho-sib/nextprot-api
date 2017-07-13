package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.Set;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class PTMInfoFormatter extends AnnotationBasedSequenceInfoFormatter {

    PTMInfoFormatter(Set<AnnotationCategory> supportedApiModel, SequenceDescriptorKey SequenceDescriptorKey) {

        super(supportedApiModel, SequenceDescriptorKey);
    }

    protected abstract String getModAccession(Annotation annotation);
    //protected abstract String getModName(Annotation annotation);

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(getModAccession(annotation))
                .append("|")
                //.append(getModName(annotation))
        ;
    }
}
