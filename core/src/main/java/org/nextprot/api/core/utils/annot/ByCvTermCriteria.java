package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Find annotations by cv term accession code
 *
 * Created by fnikitin on 02/08/16.
 */
public class ByCvTermCriteria implements SimilarityCriteria {

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return annotation1.getCvTermAccessionCode().equals(annotation2.getCvTermAccessionCode());
    }
}
