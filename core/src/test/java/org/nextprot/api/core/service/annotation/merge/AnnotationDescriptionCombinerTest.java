package org.nextprot.api.core.service.annotation.merge;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.EntityName;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.EntityNameService;
import org.nextprot.api.core.service.annotation.comp.ByIsoformPositionComparatorTest;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class AnnotationDescriptionCombinerTest {

    @Test
    public void testCombinePtmWithPtmAndEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(
                mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphotyrosine", "phosphotyrosine; by ABL1");
        Assert.assertEquals("Phosphotyrosine; by ABL1", desc);
    }

    @Test
    public void testCombinePtmAndAlternateWithPtmAndEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(
                mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; alternate", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; alternate; by ABL1", desc);
    }

    @Test
    public void testCombinePtmAndEnzymeWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by CHEK2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and CHEK2", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by CHEK2, CK1 and PLK3", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1, CHEK2, CK1 and PLK3", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndSameEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by CHEK2, CK1 and PLK3", "phosphoserine; by CK1");
        Assert.assertEquals("Phosphoserine; by CHEK2, CK1 and PLK3", desc);
    }

    @Test
    public void testCombinePtmAndAlternativeEnzymesWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by PKB/AKT1 or PKB/AKT2", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1 and PKB/AKT1 or PKB/AKT2", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesWithPtmAndOtherEnzymeAutocatalysis() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by PKA and autocatalysis", "phosphoserine; by ABL1");
        Assert.assertEquals("Phosphoserine; by ABL1, PKA and autocatalysis", desc);
    }

    @Test
    public void testCombinePtmAndEnzymesAndInvitroWithPtmAndOtherEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphothreonine; by CDK5; in vitro", "phosphothreonine; by ABL1");
        Assert.assertEquals("Phosphothreonine; by ABL1 and CDK5; in vitro", desc);
    }

    // TODO: not sure about the use case
    @Ignore
    @Test
    public void testCombinePtmAndAlternativeEnzymesWithPtmAndSameEnzyme() {

        AnnotationDescriptionCombiner combiner = new AnnotationDescriptionCombiner(mockAnnotation(),
                mockEntityNameService("spongebob"));

        String desc = combiner.combine("Phosphoserine; by PKB/AKT1 or PKB/AKT2", "phosphoserine; by AKT2");
        Assert.assertEquals("Phosphoserine; by PKB/AKT1 or PKB/AKT2", desc);
    }

    public static EntityNameService mockEntityNameService(String geneName) {

        EntityNameService service = Mockito.mock(EntityNameService.class);

        Mockito.when(service.findNamesByEntityNameClass(any(String.class), eq(Overview.EntityNameClass.GENE_NAMES)))
                .thenReturn(Collections.singletonList(mockGeneEntityName(geneName)));

        return service;
    }

    public static EntityName mockGeneEntityName(String geneName) {

        EntityName entityName = new EntityName();
        entityName.setMain(true);
        entityName.setName(geneName);
        return entityName;
    }

    public static Annotation mockAnnotation() {

        return ByIsoformPositionComparatorTest.mockAnnotation(1, AnnotationCategory.MODIFIED_RESIDUE, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P51610-1", 1, 1));
    }
}