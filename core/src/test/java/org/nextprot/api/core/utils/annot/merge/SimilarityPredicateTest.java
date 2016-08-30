package org.nextprot.api.core.utils.annot.merge;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class SimilarityPredicateTest {

    @Test
    public void newSimilarityPredicateShouldReturnNewInstanceIfDefinedCategory() throws Exception {

        Assert.assertNotNull(SimilarityPredicate.newSimilarityPredicate(AnnotationCategory.VARIANT));
    }

    @Test
    public void newSimilarityPredicateShouldReturnNullIfNotDefinedForCategory() throws Exception {

        Assert.assertNull(SimilarityPredicate.newSimilarityPredicate(AnnotationCategory.ANTIBODY_MAPPING));
    }
}