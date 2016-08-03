package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

public class AccessibleObjectSimilarityPredicateTest {

    @Test
    public void sameObjectShouldBeSimilar() throws Exception {

        SimilarityPredicate predicate = new AccessibleObjectSimilarityPredicate(Annotation::getAPICategory);

        Annotation annot = mockAnnotation(AnnotationCategory.VARIANT);

        Assert.assertTrue(predicate.isSimilar(annot, annot));
    }

    @Test
    public void shouldBeSimilar() throws Exception {

        SimilarityPredicate predicate = new AccessibleObjectSimilarityPredicate(Annotation::getAPICategory);

        Assert.assertTrue(predicate.isSimilar(mockAnnotation(AnnotationCategory.VARIANT), mockAnnotation(AnnotationCategory.VARIANT)));
    }

    @Test
    public void shouldBeDifferent() throws Exception {

        SimilarityPredicate predicate = new AccessibleObjectSimilarityPredicate(Annotation::getAPICategory);

        Assert.assertFalse(predicate.isSimilar(mockAnnotation(AnnotationCategory.VARIANT), mockAnnotation(AnnotationCategory.MUTAGENESIS)));
    }

    public static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }
}