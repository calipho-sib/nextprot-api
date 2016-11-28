package org.nextprot.api.core.utils.annot.comp;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationVariant;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class ByAnnotationVariantComparatorTest {

    @Test
    public void compareVariantsSame() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertEquals(0, cmp);
    }

    @Test
    public void compareVariantsMutagenesisSamePositionSameVariation() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.MUTAGENESIS, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertEquals(0, cmp);
    }

    @Test(expected = NextProtException.class)
    public void comparingNonVariantsThrowNextprotException() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.MUTAGENESIS, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        comparator.compare(new Annotation(), variant2);
    }

    @Test
    public void compareVariantsSamePositionDiffVariant() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "W",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "L",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertTrue(cmp>0);
    }

    @Test
    public void compareVariantsSamePositionDiffOriginal() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.VARIANT, "C", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertTrue(cmp<0);
    }

    @Test
    public void comparePositionOfLowestLocatedFeature() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 1489, 1489),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 36));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 17378, 18000),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 36));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertEquals(0, cmp);
    }

    @Test
    public void compareVariantsDiffEndingLocation() throws Exception {

        ByAnnotationVariantComparator comparator = new ByAnnotationVariantComparator();

        Annotation variant1 = mockAnnotationVariant(AnnotationCategory.VARIANT, "X", "W",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 36));

        Annotation variant2 = mockAnnotationVariant(AnnotationCategory.VARIANT, "A", "V",
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 14, 14),
                new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-4", 12, 12));

        int cmp = comparator.compare(variant1, variant2);

        Assert.assertEquals(-1, cmp);
    }

    private static Annotation mockAnnotationVariant(AnnotationCategory category, String original, String variant, ByIsoformPositionComparatorTest.TargetIsoform... targets) {

        Annotation mock = Mockito.mock(Annotation.class);

        when(mock.getVariant()).thenReturn(new AnnotationVariant(original, variant, ""));
        when(mock.getAPICategory()).thenReturn(category);

        Map<String, AnnotationIsoformSpecificity> map = new HashMap<>();
        for (ByIsoformPositionComparatorTest.TargetIsoform target : targets) {

            AnnotationIsoformSpecificity specificity = Mockito.mock(AnnotationIsoformSpecificity.class);
            when(specificity.getFirstPosition()).thenReturn(target.getStart());
            when(specificity.getLastPosition()).thenReturn(target.getEnd());
            when(specificity.getIsoformAccession()).thenReturn(target.getIsoformAccession());

            when(mock.getStartPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getStart());
            when(mock.getEndPositionForIsoform(target.getIsoformAccession())).thenReturn(target.getEnd());

            map.put(target.getIsoformAccession(), specificity);
        }

        when(mock.getTargetingIsoformsMap()).thenReturn(map);

        return mock;
    }
}