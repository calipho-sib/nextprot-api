package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

public class SimilarAnnotationGroupTest {

    @Test
    public void testAddAnnotation() {

        SimilarGroupBuilder.SimilarAnnotationGroup cluster =
                new SimilarGroupBuilder.SimilarAnnotationGroup(mockAnnotation(AnnotationCategory.VARIANT));

        Assert.assertEquals(1, cluster.size());
        Assert.assertEquals(AnnotationCategory.VARIANT, cluster.getCategory());
    }

    @Test(expected = SimilarGroupBuilder.SimilarAnnotationGroup.InvalidAnnotationGroupCategoryException.class)
    public void addAnnotationFailedIfOfDifferentCategory() throws Exception {

        SimilarGroupBuilder.SimilarAnnotationGroup cluster =
                new SimilarGroupBuilder.SimilarAnnotationGroup(mockAnnotation(AnnotationCategory.VARIANT));
        cluster.add(mockAnnotation(AnnotationCategory.MUTAGENESIS));
    }

    private static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }
}