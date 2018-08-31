package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

public class AnnotationGroupTest {

    @Test
    public void testAddAnnotation() throws Exception {

        AnnotationGroup cluster = new AnnotationGroup(AnnotationCategory.VARIANT);

        Annotation annotation = mockAnnotation(AnnotationCategory.VARIANT);
        cluster.add(annotation);

        Assert.assertEquals(1, cluster.size());
        Assert.assertEquals(AnnotationCategory.VARIANT, cluster.getCategory());
    }

    @Test(expected = AnnotationGroup.InvalidAnnotationGroupCategoryException.class)
    public void addAnnotationFailedIfOfDifferentCategory() throws Exception {

        AnnotationGroup cluster = new AnnotationGroup(AnnotationCategory.VARIANT);

        Annotation annotation = mockAnnotation(AnnotationCategory.MUTAGENESIS);
        cluster.add(annotation);
    }

    private static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }
}