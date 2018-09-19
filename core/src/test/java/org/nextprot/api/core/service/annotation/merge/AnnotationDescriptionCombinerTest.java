package org.nextprot.api.core.service.annotation.merge;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.text.ParseException;

public class AnnotationDescriptionCombinerTest {

    @Test
    public void testParser() throws ParseException {

        AnnotationDescriptionCombiner.DescriptionParser parser = new AnnotationDescriptionCombiner.DescriptionParser();

        AnnotationDescriptionCombiner.Description desc = parser.parse("phosphotyrosine; by ABL1");
        Assert.assertEquals("Phosphotyrosine", desc.getPtm());
        Assert.assertEquals(Sets.newHashSet("ABL1"), desc.getEnzymes());
        Assert.assertEquals("Phosphotyrosine; by ABL1", desc.format());
    }

    @Test
    public void testCombinePtmWithPtmAndEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphotyrosine", "phosphotyrosine; by ABL1");
        Assert.assertEquals("Phosphotyrosine; by ABL1", desc);
    }

    @Test
    public void testCombinePtmAndAlternateWithPtmAndEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; alternate", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; alternate; by ABL1", desc);
    }

    @Test
    public void testCombinePtmAndEnzymeWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by CHEK2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and CHEK2", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by CHEK2, CK1 and PLK3", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1, CHEK2, CK1 and PLK3", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndSameEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by CHEK2, CK1 and PLK3", "phosphoserine; by CK1");
        Assert.assertEquals("Phosphoserine; by CHEK2, CK1 and PLK3", desc);
    }

    @Test
    public void testCombinePtmAndAlternativeEnzymesWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by PKB/AKT1 or PKB/AKT2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and PKB/AKT1 or PKB/AKT2", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndOtherEnzymeAutocatalysis() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by PKA and autocatalysis", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1, PKA and autocatalysis", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesAndInvitroWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphothreonine; by CDK5; in vitro", "phosphothreonine; by ABL1");
        Assert.assertEquals("Phosphothreonine; by ABL1 and CDK5; in vitro", desc);
    }

    // TODO: not sure about the use case
    @Ignore
    @Test
    public void testCombinePtmAndAlternativeEnzymesWithPtmAndSameEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(Mockito.mock(Annotation.class));

        String desc = combiner.combine("Phosphoserine; by PKB/AKT1 or PKB/AKT2", "phosphoserine; by AKT2");
        Assert.assertEquals("Phosphoserine; by PKB/AKT1 or PKB/AKT2", desc);
    }
}