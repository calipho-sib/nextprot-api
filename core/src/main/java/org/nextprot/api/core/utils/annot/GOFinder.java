package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Implementation of finder for GO annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class GOFinder extends AnnotationFinder {

    @Override
    protected boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return annotation1.getCvTermAccessionCode().equals(annotation2.getCvTermAccessionCode());
    }
}
