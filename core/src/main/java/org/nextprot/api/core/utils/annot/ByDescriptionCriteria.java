package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ByDescriptionCriteria implements SimilarityCriteria {

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteria.isSimilarObjects(annotation1.getDescription(), annotation2.getDescription());
    }
}
