package org.nextprot.api.core.domain.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnotationIsoformSpecificityTest {

    @Test
    public void testFormatIsoName() throws Exception {

        Assert.assertEquals("Iso 001", AnnotationIsoformSpecificity.formatIsoName("Iso 1"));
    }

    @Test
    public void testFormatIsoName2() throws Exception {

        Assert.assertEquals("Iso 010", AnnotationIsoformSpecificity.formatIsoName("Iso 10"));
    }

    @Test
    public void testFormatIsoName2NoMatch() throws Exception {

        Assert.assertEquals("NX_P01325", AnnotationIsoformSpecificity.formatIsoName("NX_P01325"));
    }

    @Test
    public void shouldHaveSameIsoformPositions() throws Exception {

        AnnotationIsoformSpecificity specificity1 = mockAnnotationIsoformSpecificity("NX_Q04771-1", 328, 328);
        AnnotationIsoformSpecificity specificity2 = mockAnnotationIsoformSpecificity("NX_Q04771-1", 328, 328);

        Assert.assertTrue(specificity1.hasSameIsoformPositions(specificity2));
    }

    @Test
    public void shouldNotHaveSameIsoformPositions() throws Exception {

        AnnotationIsoformSpecificity specificity1 = mockAnnotationIsoformSpecificity("NX_Q04771-1", 328, 328);
        AnnotationIsoformSpecificity specificity2 = mockAnnotationIsoformSpecificity("NX_Q04771-1", 327, 328);

        Assert.assertFalse(specificity1.hasSameIsoformPositions(specificity2));
    }

    @Test
    public void shouldHaveSameIsoformNullPositions() throws Exception {

        AnnotationIsoformSpecificity specificity1 = mockAnnotationIsoformSpecificity("NX_Q04771-1", null, null);
        AnnotationIsoformSpecificity specificity2 = mockAnnotationIsoformSpecificity("NX_Q04771-1", null, null);

        Assert.assertTrue(specificity1.hasSameIsoformPositions(specificity2));
    }

    @Test
    public void shouldReturnIsoformsInProperOrder() {

        List<AnnotationIsoformSpecificity> specs = newIsoSpecs(13);
        Collections.sort(specs);

        int i=0;
        for (AnnotationIsoformSpecificity spec: specs) {
            i++;
            Assert.assertEquals("Iso " + i, spec.getIsoformAccession()); // TITIN has Iso 1, Iso 2, ... Iso 13
        }
    }

    private List<AnnotationIsoformSpecificity> newIsoSpecs(int count) {

        List<AnnotationIsoformSpecificity> specs = new ArrayList<>();

        for (int i=count ; i>0 ; i--) {
            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
            spec.setIsoformAccession("Iso "+i); // <= should be only accession in there !
            specs.add(spec);
        }

        return specs;
    }

    public static AnnotationIsoformSpecificity mockAnnotationIsoformSpecificity(String name, Integer first, Integer last) {

        AnnotationIsoformSpecificity specificity = new AnnotationIsoformSpecificity();
        specificity.setIsoformAccession(name);
        specificity.setFirstPosition(first);
        specificity.setLastPosition(last);

        return specificity;
    }
}