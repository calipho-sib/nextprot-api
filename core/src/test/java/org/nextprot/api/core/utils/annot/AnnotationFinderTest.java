package org.nextprot.api.core.utils.annot;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.when;

public class AnnotationFinderTest {

    @Test
    public void shouldNotFindAnnotFromEmptyList() throws Exception {

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

    private static Annotation mockAnnotation(AnnotationCategory cat) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(cat);

        return annotation;
    }

    private static SimilarityCriteria newApiCatCriteria() {

        return (a1, a2) -> a1.getAPICategory() == a2.getAPICategory();
    }
}