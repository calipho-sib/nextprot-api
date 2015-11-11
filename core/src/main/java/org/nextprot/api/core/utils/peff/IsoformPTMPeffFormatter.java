package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Set;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class IsoformPTMPeffFormatter extends IsoformAnnotationPeffFormatter {

    protected IsoformPTMPeffFormatter(Set<AnnotationCategory> supportedApiModel, PeffKey peffKey) {

        super(supportedApiModel, peffKey);
    }

    protected abstract String getModName(Annotation annotation);

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder("");

        for (Annotation annotation : annotations) {

            if (support(annotation))
                sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName()))
                        .append("|").append(getModName(annotation)).append(")");
        }
        return sb.toString();
    }
}
