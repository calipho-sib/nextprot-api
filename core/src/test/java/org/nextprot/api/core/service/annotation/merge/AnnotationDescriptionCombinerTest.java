package org.nextprot.api.core.service.annotation.merge;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationDescriptionCombinerTest {

    @Test
    public void testParser() {

        AnnotationDescriptionCombiner.Parser parser = new AnnotationDescriptionCombiner.Parser();

        AnnotationDescriptionCombiner.Description desc = parser.parse("phosphotyrosine; by ABL1");
        Assert.assertEquals("Phosphotyrosine", desc.getPtm());
        Assert.assertEquals(Sets.newHashSet("ABL1"), desc.getEnzymes());
        Assert.assertEquals("phosphotyrosine; by ABL1", desc.format());
    }

    @Test
    public void testCombine1() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner();

        String desc = combiner.combine("Phosphotyrosine", "phosphotyrosine; by ABL1");
        Assert.assertEquals("phosphotyrosine; by ABL1", desc);
    }

    @Test
    public void testCombine2() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner();

        String desc = combiner.combine("Phosphoserine; alternate", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; alternate; by ABL1", desc);
    }

    @Test
    public void testCombine3() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner();

        String desc = combiner.combine("Phosphoserine; by CHEK2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and CHEK2", desc);
    }

    @Test
    public void testCombine4() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner();

        String desc = combiner.combine("Phosphoserine; by CHEK2, CK1 and PLK3", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1, CHEK2, CK1 and PLK3", desc);
    }

    @Test
    public void testCombine5() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner();

        String desc = combiner.combine("Phosphoserine; by PKB/AKT1 or PKB/AKT2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and PKB/AKT1 or PKB/AKT2", desc);
    }
}