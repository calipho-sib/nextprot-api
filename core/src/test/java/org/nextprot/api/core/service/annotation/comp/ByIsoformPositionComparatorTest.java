package org.nextprot.api.core.service.annotation.comp;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;

import java.util.*;

import static org.mockito.Mockito.when;

public class ByIsoformPositionComparatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstrFromNonCanonicalIsoform() throws Exception {

        new ByIsoformPositionComparator(mockIsoform(false));
    }

    @Test
    public void testCompAnnotsSingleIso() {

        ByIsoformPositionComparator comparator = new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true));

        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 172, 172)));
        annotations.add(mockAnnotation(2, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 89, 89)));
        annotations.add(mockAnnotation(3, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 76, 76)));
        annotations.add(mockAnnotation(4, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 72, 72)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 4, 3, 2, 1);
    }

    @Test
    public void testCompAnnotsSingleIsoSameStartPos() {

        ByIsoformPositionComparator comparator = new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true));
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 10)));
        annotations.add(mockAnnotation(2, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 20)));
        annotations.add(mockAnnotation(3, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(4, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 40)));
        annotations.add(mockAnnotation(5, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 50)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 5, 4, 3, 2, 1);
    }

    @Test
    public void testCompAnnotsSingleIsoSamePos() {

        ByIsoformPositionComparator comparator = new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true));
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 10)));
        annotations.add(mockAnnotation(2, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 20)));
        annotations.add(mockAnnotation(3, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(4, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 30)));
        annotations.add(mockAnnotation(5, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 1, 30)));

        Collections.sort(annotations, comparator.thenComparingLong(Annotation::getAnnotationId));

        assertExpectedIds(annotations, 3, 4, 5, 2, 1);
    }

    @Test
    public void testCompAnnotsMultipleIsos() {

        ByIsoformPositionComparator comparator = new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true));
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 23, 100), new TargetIsoform("NX_P51610-2", 1, 19),  new TargetIsoform("NX_P51610-3", 1, 129)));
        annotations.add(mockAnnotation(2, AnnotationCategory.VARIANT, new TargetIsoform("NX_P51610-1", 2, 10), new TargetIsoform("NX_P51610-2", 1, 5),  new TargetIsoform("NX_P51610-3", 1, 10)));

        Collections.sort(annotations, comparator);

        assertExpectedIds(annotations, 2, 1);
    }

    @Test
    public void canonicalAnnotsShouldComesFirst() {

        ByIsoformPositionComparator comparator = new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true));
        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-1", 2, 1423), new TargetIsoform("NX_P51610-4", 2, 1423)));
        annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-2", 2, 1354)));
        annotations.add(mockAnnotation(3, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-1", 2, 1323), new TargetIsoform("NX_P51610-4", 2, 1323)));
        annotations.add(mockAnnotation(4, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-2", 2, 1254)));
        annotations.add(mockAnnotation(5, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-2", 2, 1226)));

        annotations.sort(comparator);

        assertExpectedIds(annotations, 1, 3, 2, 4, 5);
    }

    @Test
    public void shouldSortByAnnotIdIfEqualPos() {

        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-3", 2, 1423)));
        annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-2", 2, 1423)));
        annotations.add(mockAnnotation(3, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-4", 2, 1423)));

        annotations.sort(new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true)));

        assertExpectedIds(annotations, 1, 2, 3);
    }

    @Test
    public void shouldIsoFirstThenByIsoName() {

        List<Annotation> annotations = new ArrayList<>();

        annotations.add(mockAnnotation(3, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-3", 2, 1423)));
        annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-2", 2, 1423)));
        annotations.add(mockAnnotation(4, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-4", 2, 1423)));
        annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new TargetIsoform("NX_P51610-1", 2, 1423)));

        annotations.sort(new ByIsoformPositionComparator(mockIsoform("NX_P51610-1", true)));

        assertExpectedIds(annotations, 1, 2, 3, 4);
    }

    private static Isoform mockIsoform(boolean isCanonical) {

        Isoform isoform = Mockito.mock(Isoform.class);

        when(isoform.isCanonicalIsoform()).thenReturn(isCanonical);

        return isoform;
    }

    private static Isoform mockIsoform(String accession, boolean isCanonical) {

        Isoform isoform = Mockito.mock(Isoform.class);

        when(isoform.isCanonicalIsoform()).thenReturn(isCanonical);
        when(isoform.getIsoformAccession()).thenReturn(accession);

        return isoform;
    }

    public static Annotation mockAnnotation(long id, AnnotationCategory cat, TargetIsoform... targets) {

        Annotation mock = Mockito.mock(Annotation.class);

        when(mock.getAnnotationId()).thenReturn(id);
        when(mock.getAPICategory()).thenReturn(cat);

        Map<String, AnnotationIsoformSpecificity> map = new HashMap<>();
        for (TargetIsoform target : targets) {

            AnnotationIsoformSpecificity specificity = Mockito.mock(AnnotationIsoformSpecificity.class);
            when(specificity.getFirstPosition()).thenReturn(target.getStart());
            when(specificity.getLastPosition()).thenReturn(target.getEnd());
            when(specificity.getIsoformAccession()).thenReturn(target.getIsoformAccession());
            when(specificity.getAnnotationId()).thenReturn(id);

            when(mock.getStartPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getStart());
            when(mock.getEndPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getEnd());

            map.put(target.getIsoformAccession(), specificity);
        }

        when(mock.getTargetingIsoformsMap()).thenReturn(map);

        return mock;
    }

    private static void assertExpectedIds(List<Annotation> observedAnnots, long... expectedAnnotIds) {

        Assert.assertEquals(observedAnnots.size(), expectedAnnotIds.length);

        int i=0;
        for (Annotation observedAnnot : observedAnnots) {

            Assert.assertEquals(expectedAnnotIds[i++], observedAnnot.getAnnotationId());
        }
    }

    public static class TargetIsoform {

        private final String isoformAccession;
        private final Integer start;
        private final Integer end;

        public TargetIsoform(String isoformAccession, Integer start, Integer end) {

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