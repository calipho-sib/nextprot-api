package org.nextprot.api.core.utils.annot;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

/**
 * Find annotations by cv term accession code
 *
 * Created by fnikitin on 02/08/16.
 */
public class SimilarityCriteriaList implements SimilarityCriteria {

    private final List<SimilarityCriteria> criteria;

    public SimilarityCriteriaList(List<SimilarityCriteria> criteria) {

        Preconditions.checkNotNull(criteria);
        this.criteria = criteria;
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        for (SimilarityCriteria criterium : criteria) {

            if (!criterium.isSimilar(annotation1, annotation2))
                return false;
        }

        return true;
    }
}
