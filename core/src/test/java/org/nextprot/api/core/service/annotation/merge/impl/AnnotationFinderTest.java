package org.nextprot.api.core.service.annotation.merge.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationSimilarityPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.nextprot.api.core.service.annotation.merge.impl.ObjectAnnotationSimilarityPredicateTest.mockAnnotation;

public class AnnotationFinderTest {

    @Test
    public void shouldNotFindAnnotInEmptyList() {

        AnnotationFinder finder = new AnnotationFinder();

        Optional<Annotation> optAnnotation = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                new ArrayList<>()
        );

        Assert.assertTrue(!optAnnotation.isPresent());
    }

    @Test
    public void shouldFindSameAnnot() {

        AnnotationFinder finder = new AnnotationFinder() {
            @Override
            protected AnnotationSimilarityPredicate newPredicate(Annotation annotation) {
                return newApiCatCriteria();
            }
        };

        Optional<Annotation> optAnnotation = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Collections.singletonList(mockAnnotation(AnnotationCategory.VARIANT))
        );

        Assert.assertTrue(optAnnotation.isPresent());
        Assert.assertEquals(AnnotationCategory.VARIANT, optAnnotation.get().getAPICategory());
    }

    @Test
    public void shouldNotFindDiffAnnot() {

        AnnotationFinder finder = new AnnotationFinder();

        Optional<Annotation> optAnnotation = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Collections.singletonList(mockAnnotation(AnnotationCategory.MUTAGENESIS))
        );

        Assert.assertTrue(!optAnnotation.isPresent());
    }

    @Test
    public void shouldFindOneAnnotIfMultipleMatches() {

        AnnotationFinder finder = new AnnotationFinder() {
            @Override
            protected AnnotationSimilarityPredicate newPredicate(Annotation annotation) {
                return newApiCatCriteria();
            }
        };

        Annotation annot = new Annotation();
        annot.setAnnotationName("joe");
        annot.setAnnotationCategory(AnnotationCategory.VARIANT);

        Optional<Annotation> optAnnotation = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Arrays.asList(annot, mockAnnotation(AnnotationCategory.VARIANT))
        );

        Assert.assertTrue(optAnnotation.isPresent());
        Assert.assertEquals(AnnotationCategory.VARIANT, optAnnotation.get().getAPICategory());
        Assert.assertEquals("joe", optAnnotation.get().getAnnotationName());
    }

    private static AnnotationSimilarityPredicate newApiCatCriteria() {

        return (a1, a2) -> a1.getAPICategory() == a2.getAPICategory();
    }
}