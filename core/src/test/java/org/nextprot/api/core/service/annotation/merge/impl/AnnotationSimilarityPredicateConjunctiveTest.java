package org.nextprot.api.core.service.annotation.merge.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationSimilarityPredicate;

import java.util.Arrays;
import java.util.Collections;

public class AnnotationSimilarityPredicateConjunctiveTest {

    @Test
    public void shouldBeSimilarWhenAllPredicateTrue() throws Exception {

        AnnotationSimilarityPredicate predicate = new SimilarityPredicateConjunctive(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true
        ));

        Assert.assertTrue(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constrShouldThrowExceptionIfEmpty() throws Exception {

        new SimilarityPredicateConjunctive(Collections.emptyList());
    }

    @Test
    public void shouldNotBeSimilarWhenAtLeastOnePredicateFalse() throws Exception {

        AnnotationSimilarityPredicate predicate = new SimilarityPredicateConjunctive(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> false,
                (annotation1, annotation2) -> true
        ));

        Assert.assertFalse(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }
}