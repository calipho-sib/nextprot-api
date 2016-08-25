package org.nextprot.api.core.utils.annot.comp;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ByAnnotationSubjectComparatorTest {

    @Test
    public void compareAnnotationsUndefinedSubjectComponents() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(new HashMap<>());

        Annotation a1 = new Annotation();
        Annotation a2 = new Annotation();

        int cmp = comparator.compare(a1, a2);
        Assert.assertEquals(0, cmp);
    }

    @Test
    public void compareAnnotationsFirstDefinedSubjectComponent() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(new HashMap<>());

        Annotation a1 = new Annotation();
        a1.setSubjectComponents(Collections.emptyList());

        int cmp = comparator.compare(a1, new Annotation());
        Assert.assertEquals(-1, cmp);
    }

    @Test
    public void compareAnnotationsSecondDefinedSubjectComponent() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(new HashMap<>());

        Annotation a2 = new Annotation();
        a2.setSubjectComponents(Collections.emptyList());

        int cmp = comparator.compare(new Annotation(), a2);
        Assert.assertEquals(1, cmp);
    }

    @Test(expected = NextProtException.class)
    public void compareAnnotationsCannotFindSubjectAnnot() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(new HashMap<>());

        Annotation a1 = new Annotation();
        a1.setSubjectComponents(Collections.singletonList("hash1"));
        Annotation a2 = new Annotation();
        a2.setSubjectComponents(Collections.singletonList("hash2"));

        int cmp = comparator.compare(a1, a2);
        Assert.assertEquals(0, cmp);
    }

    @Test(expected = NextProtException.class)
    public void compareAnnotationsCannotFindOneSubjectAnnot() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(newHashMap(
                mockAnnotation(AnnotationCategory.VARIANT, "hash1"))
        );

        Annotation a1 = new Annotation();
        a1.setSubjectComponents(Collections.singletonList("hash1"));
        Annotation a2 = new Annotation();
        a2.setSubjectComponents(Collections.singletonList("hash2"));

        int cmp = comparator.compare(a1, a2);
        Assert.assertEquals(0, cmp);
    }

    @Test
    public void compareAnnotations() throws Exception {

        ByAnnotationSubjectComparator comparator = new ByAnnotationSubjectComparator(newHashMap(
                    mockAnnotation(AnnotationCategory.VARIANT, "hash1"),
                    mockAnnotation(AnnotationCategory.MUTAGENESIS, "hash2")
                ),
                mockHashableComparator(0)
        );

        Annotation a1 = new Annotation();
        a1.setSubjectComponents(Collections.singletonList("hash1"));
        Annotation a2 = new Annotation();
        a2.setSubjectComponents(Collections.singletonList("hash2"));

        int cmp = comparator.compare(a1, a2);
        Assert.assertEquals(0, cmp);
    }

    public static Map<String, Annotation> newHashMap(Annotation... annotations) {

        Map<String, Annotation> map = new HashMap<>();

        for (Annotation annotation : annotations) {

            map.put(annotation.getAnnotationHash(), annotation);
        }

        return map;
    }

    public static Annotation mockAnnotation(AnnotationCategory annotationCategory, String annotationHash) {

        Annotation annotation = Mockito.mock(Annotation.class);

        when(annotation.getAPICategory()).thenReturn(annotationCategory);
        when(annotation.getAnnotationHash()).thenReturn(annotationHash);

        return annotation;
    }

    public static Comparator<Annotation> mockHashableComparator(int cmpReturn) {

        Comparator<Annotation> comparator = Mockito.mock(Comparator.class);

        when(comparator.compare(any(Annotation.class), any(Annotation.class))).thenReturn(cmpReturn);

        return comparator;
    }
}