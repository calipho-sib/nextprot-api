package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.nextprot.api.core.utils.annot.ObjectSimilarityPredicateTest.mockAnnotation;

public class AnnotationFinderTest {

    @Test
    public void shouldNotFindAnnotInEmptyList() throws Exception {

        AnnotationFinder finder = new AnnotationFinder(newApiCatCriteria());

        Annotation found = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                new ArrayList<>()
        );

        Assert.assertNull(found);
    }

    @Test
    public void shouldFindSameAnnot() throws Exception {

        AnnotationFinder finder = new AnnotationFinder(newApiCatCriteria());

        Annotation found = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Collections.singletonList(mockAnnotation(AnnotationCategory.VARIANT))
        );

        Assert.assertNotNull(found);
        Assert.assertEquals(AnnotationCategory.VARIANT, found.getAPICategory());
    }

    @Test
    public void shouldNotFindDiffAnnot() throws Exception {

        AnnotationFinder finder = new AnnotationFinder(newApiCatCriteria());

        Annotation found = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Collections.singletonList(mockAnnotation(AnnotationCategory.MUTAGENESIS))
        );

        Assert.assertNull(found);
    }

    @Test
    public void shouldFindOneAnnotIfMultipleMatches() throws Exception {

        AnnotationFinder finder = new AnnotationFinder(newApiCatCriteria());

        Annotation annot = new Annotation();
        annot.setAnnotationName("joe");
        annot.setCategory(AnnotationCategory.VARIANT);

        Annotation found = finder.find(
                mockAnnotation(AnnotationCategory.VARIANT),
                Arrays.asList(annot, mockAnnotation(AnnotationCategory.VARIANT))
        );

        Assert.assertNotNull(found);
        Assert.assertEquals(AnnotationCategory.VARIANT, found.getAPICategory());
        Assert.assertEquals("joe", found.getAnnotationName());
    }

    private static SimilarityPredicate newApiCatCriteria() {

        return (a1, a2) -> a1.getAPICategory() == a2.getAPICategory();
    }
}