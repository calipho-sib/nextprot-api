package org.nextprot.api.core.domain.annotation;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fnikitin on 19/10/15.
 */
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
    public void shouldReturnIsoformsInProperOrder() {

        List<AnnotationIsoformSpecificity> specs = newIsoSpecs(13);
        Collections.sort(specs);

        int i=0;
        for (AnnotationIsoformSpecificity spec: specs) {
            i++;
            Assert.assertEquals("Iso " + i, spec.getIsoformName()); // TITIN has Iso 1, Iso 2, ... Iso 13
        }
    }

    private List<AnnotationIsoformSpecificity> newIsoSpecs(int count) {

        List<AnnotationIsoformSpecificity> specs = new ArrayList<>();

        for (int i=count ; i>0 ; i--) {
            AnnotationIsoformSpecificity spec = new AnnotationIsoformSpecificity();
            spec.setIsoformName("Iso "+i);
            specs.add(spec);
        }

        return specs;
    }
}