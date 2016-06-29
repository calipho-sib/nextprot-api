package com.nextprot.api.isoform.mapper.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class MappedIsoformsFeatureResultTest {

    @Test
    public void testStandardQuery() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals("SCN11A-p.Leu1158Pro", query.getFeature());
        Assert.assertEquals("sequence variant", query.getFeatureType());
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

        MappedIsoformsFeatureError result = new MappedIsoformsFeatureError(query);
        result.setErrorValue(new MappedIsoformsFeatureError.InvalidPosition("invalid position on NX_Q9UI33", 23));
    }

    @Test
    public void testOnUnexpectedAminoAcidsError() {

        MappedIsoformsFeatureResult.Query query =
                new MappedIsoformsFeatureResult.Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new MappedIsoformsFeatureError(query);
        result.setErrorValue(new MappedIsoformsFeatureError.UnexpectedAminoAcids("A", "P"));
    }
}