package com.nextprot.api.isoform.mapper.domain;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class MappedIsoformsFeatureResultTest {

    @Test
    public void testStandardQuery() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals("SCN11A-p.Leu1158Pro", query.getFeature());
        Assert.assertEquals("Variant", query.getFeatureType());
        Assert.assertTrue(query.isPropagate());
    }

    /*
    MappedIsoformsFeatureResult mapping = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33", true);

        Assert.assertEquals(3, mapping.countMappedIsoformFeatureResults());
    assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-1"), 1158, 1158, MappedIsoformFeatureResult.Status.MAPPED);
    assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-2"), 1158, 1158, MappedIsoformFeatureResult.Status.MAPPED);
    assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-3"), 1120, 1120, MappedIsoformFeatureResult.Status.MAPPED);
*/

    @Test
    public void testOnSuccess() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureSuccess result = new MappedIsoformsFeatureSuccess(query);
        result.addMappedIsoformFeature("NX_Q9UI33-1", 1158, 1158);
        result.addMappedIsoformFeature("NX_Q9UI33-2", 1158, 1158);
        result.addMappedIsoformFeature("NX_Q9UI33-3", 1120, 1120);
    }

    @Test
    public void testOnInvalidPositionError() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new MappedIsoformsFeatureError.InvalidFeaturePosition(query, 23);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature position: position 23 is out of bound in sequence of isoform NX_Q9UI33", result.getMessage());
    }

    @Test
    public void testOnUnexpectedAminoAcidsError() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new MappedIsoformsFeatureError.InvalidFeatureAminoAcid(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.Alanine), AminoAcidCode.asArray(AminoAcidCode.Leucine));

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature specification: found amino-acid Ala at position 1158 of sequence isoform NX_Q9UI33 instead of Leu as incorrectly specified in feature 'SCN11A-p.Leu1158Pro'", result.getMessage());
    }

    @Test
    public void testOnIncompatibleProteinAndGeneNameError() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_P01308", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new MappedIsoformsFeatureError.IncompatibleGeneAndProteinName(query, "SCN11A", Lists.newArrayList("INS"));

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("gene/protein incompatibility: protein NX_P01308 is not compatible with gene SCN11A (expected genes: [INS])", result.getMessage());
    }
}