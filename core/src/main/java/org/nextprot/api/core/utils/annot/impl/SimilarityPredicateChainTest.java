package org.nextprot.api.core.utils.annot.impl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.SimilarityPredicate;

import java.util.Arrays;
import java.util.Collections;

public class SimilarityPredicateChainTest {

    @Test
    public void shouldBeSimilarWhenAllPredicateTrue() throws Exception {

        SimilarityPredicate predicate = new SimilarityPredicateChain(Arrays.asList(
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

        SimilarityPredicate predicate = new SimilarityPredicateChain(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> false,
                (annotation1, annotation2) -> true
        ));

        Assert.assertFalse(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }
}