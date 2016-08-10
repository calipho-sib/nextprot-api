package org.nextprot.api.core.utils.annot.merge;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import static org.mockito.Mockito.when;

public class AnnotationClusterTest {

    @Test
    public void testAddAnnotation() throws Exception {

        AnnotationCluster cluster = new AnnotationCluster(AnnotationCategory.VARIANT);

        Annotation annotation = mockAnnotation(AnnotationCategory.VARIANT);
        cluster.add(annotation);

        Assert.assertEquals(1, cluster.size());
        Assert.assertEquals(AnnotationCategory.VARIANT, cluster.getCategory());
    }

    @Test(expected = AnnotationCluster.InvalidAnnotationClusterCategoryException.class)
    public void addAnnotationFailedIfOfDifferentCategory() throws Exception {

        AnnotationCluster cluster = new AnnotationCluster(AnnotationCategory.VARIANT);

        Annotation annotation = mockAnnotation(AnnotationCategory.MUTAGENESIS);
        cluster.add(annotation);
    }

    private static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }
}