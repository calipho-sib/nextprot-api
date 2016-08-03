package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Arrays;
import java.util.Collections;

public class SimilarityPredicatePipelineTest {

    @Test
    public void shouldBeSimilarWhenAllPredicateTrue() throws Exception {

        SimilarityPredicate predicate = new SimilarityPredicatePipeline(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> true
        ));

        Assert.assertTrue(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constrShouldThrowExceptionIfEmpty() throws Exception {

        new SimilarityPredicatePipeline(Collections.emptyList());
    }

    @Test
    public void shouldNotBeSimilarWhenAtLeastOnePredicateFalse() throws Exception {

        SimilarityPredicate predicate = new SimilarityPredicatePipeline(Arrays.asList(
                (annotation1, annotation2) -> true,
                (annotation1, annotation2) -> false,
                (annotation1, annotation2) -> true
        ));

        Assert.assertFalse(predicate.isSimilar(Mockito.mock(Annotation.class), Mockito.mock(Annotation.class)));
    }
}