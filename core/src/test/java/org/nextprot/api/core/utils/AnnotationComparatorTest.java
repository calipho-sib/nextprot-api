package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.*;

public class AnnotationComparatorTest {

    @Test
    public void testComparePositionAsc() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(2, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionDesc() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(2, 25, false);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionWithBeginNull() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(null, 25, true);

        Assert.assertTrue(cmp < 0);
    }

    @Test
    public void testComparePositionWithBeginNull2() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(19, null, true);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testComparePositionBothNull() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(null, null, true);

        Assert.assertTrue(cmp == 0);
    }

    @Test
    public void testCompareDifferentEnds() throws Exception {

        AnnotationComparator comparator = new AnnotationComparator(Mockito.mock(Isoform.class));

        int cmp = comparator.compare(2, 1012, 1L, 2, 1042, 2L);

        Assert.assertTrue(cmp > 0);
    }

    @Test
    public void testCompAnnotsSingleIso() {

        Isoform canonical = new Isoform();
        canonical.setSwissProtDisplayedIsoform(true);
        canonical.setUniqueName("NX_P51610-1");

        AnnotationComparator comparator = new AnnotationComparator(canonical);

        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, "variant", new TargetIsoform("NX_P51610-1", 172, 172)));
        annotations.add(mockAnnotation(2, "variant", new TargetIsoform("NX_P51610-1", 89, 89)));
        annotations.add(mockAnnotation(3, "variant", new TargetIsoform("NX_P51610-1", 76, 76)));
        annotations.add(mockAnnotation(4, "variant", new TargetIsoform("NX_P51610-1", 72, 72)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 4, 3, 2, 1);
    }

    @Test
    public void testCompAnnotsSingleIsoSameStartPos() {

        Isoform canonical = new Isoform();
        canonical.setSwissProtDisplayedIsoform(true);
        canonical.setUniqueName("NX_P51610-1");

        AnnotationComparator comparator = new AnnotationComparator(canonical);
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, "variant", new TargetIsoform("NX_P51610-1", 1, 10)));
        annotations.add(mockAnnotation(2, "variant", new TargetIsoform("NX_P51610-1", 1, 20)));
        annotations.add(mockAnnotation(3, "variant", new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(4, "variant", new TargetIsoform("NX_P51610-1", 1, 40)));
        annotations.add(mockAnnotation(5, "variant", new TargetIsoform("NX_P51610-1", 1, 50)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 5, 4, 3, 2, 1);
    }

    @Test
    public void testCompAnnotsSingleIsoSamePos() {

        Isoform canonical = new Isoform();
        canonical.setSwissProtDisplayedIsoform(true);
        canonical.setUniqueName("NX_P51610-1");

        AnnotationComparator comparator = new AnnotationComparator(canonical);
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, "variant", new TargetIsoform("NX_P51610-1", 1, 10)));
        annotations.add(mockAnnotation(2, "variant", new TargetIsoform("NX_P51610-1", 1, 20)));
        annotations.add(mockAnnotation(3, "variant", new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(4, "variant", new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(5, "variant", new TargetIsoform("NX_P51610-1", 1, 30)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 3, 4, 5, 2, 1);
    }

    @Test
    public void testCompAnnotsMultipleIsos() {

        Isoform canonical = new Isoform();
        canonical.setSwissProtDisplayedIsoform(true);
        canonical.setUniqueName("NX_P51610-1");

        AnnotationComparator comparator = new AnnotationComparator(canonical);
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, "variant", new TargetIsoform("NX_P51610-1", 23, 100), new TargetIsoform("NX_P51610-2", 1, 19),  new TargetIsoform("NX_P51610-3", 1, 129)));
        annotations.add(mockAnnotation(2, "variant", new TargetIsoform("NX_P51610-1", 2, 10), new TargetIsoform("NX_P51610-2", 1, 5),  new TargetIsoform("NX_P51610-3", 1, 10)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 2, 1);
    }

    private static Annotation mockAnnotation(long id, String cat, TargetIsoform... targets) {

        Annotation mock = Mockito.mock(Annotation.class);

        Mockito.when(mock.getAnnotationId()).thenReturn(id);
        Mockito.when(mock.getCategory()).thenReturn(cat);

        Map<String, AnnotationIsoformSpecificity> map = new HashMap<>();
        for (TargetIsoform target : targets) {

            AnnotationIsoformSpecificity specificity = Mockito.mock(AnnotationIsoformSpecificity.class);
            Mockito.when(specificity.getFirstPosition()).thenReturn(target.getStart());
            Mockito.when(specificity.getLastPosition()).thenReturn(target.getEnd());
            Mockito.when(specificity.getIsoformName()).thenReturn(target.getIsoformAccession());
            Mockito.when(specificity.getAnnotationId()).thenReturn(id);

            Mockito.when(mock.getStartPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getStart());
            Mockito.when(mock.getEndPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getEnd());

            map.put(target.getIsoformAccession(), specificity);
        }

        Mockito.when(mock.getTargetingIsoformsMap()).thenReturn(map);

        return mock;
    }

    private static void assertExpectedIds(List<Annotation> observedAnnots, long... expectedAnnotIds) {

        Assert.assertEquals(observedAnnots.size(), expectedAnnotIds.length);

        int i=0;
        for (Annotation observedAnnot : observedAnnots) {

            Assert.assertEquals(expectedAnnotIds[i++], observedAnnot.getAnnotationId());
        }
    }

    private static class TargetIsoform {

        private final String isoformAccession;
        private final Integer start;
        private final Integer end;

        TargetIsoform(String isoformAccession, Integer start, Integer end) {

            this.isoformAccession = isoformAccession;
            this.start = start;
            this.end = end;
        }

        public String getIsoformAccession() {
            return isoformAccession;
        }

        public Integer getStart() {
            return start;
        }

        public Integer getEnd() {
            return end;
        }
    }
}