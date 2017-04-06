package org.nextprot.api.core.utils.annot.merge.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationSimilarityPredicate;

import java.util.Arrays;
import java.util.Collections;

public class AnnotationSimilarityPredicateChainTest {

    @Test
    public void shouldBeSimilarWhenAllPredicateTrue() throws Exception {

        AnnotationSimilarityPredicate predicate = new SimilarityPredicateChain(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true
        ));

        Assert.assertTrue(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constrShouldThrowExceptionIfEmpty() throws Exception {

        new SimilarityPredicateChain(Collections.emptyList());
    }

    @Test
    public void shouldNotBeSimilarWhenAtLeastOnePredicateFalse() throws Exception {

        AnnotationSimilarityPredicate predicate = new SimilarityPredicateChain(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> false,
                (annotation1, annotation2) -> true
        ));

        Assert.assertFalse(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }
}