package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureSuccess;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;

public class IsoformMappingServiceTest extends IsoformMappingBaseTest {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    private IsoformMappingService service;

    // http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=4408659
    @Test
    public void shouldValidateFeatureOnCanonicalIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        /*assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-1"), 1158, 1158, MappedIsoformFeatureResult.Status.MAPPED);
        assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-2"), 1158, 1158, MappedIsoformFeatureResult.Status.MAPPED);
        assertIsoformFeature(mapping.countMappedIsoformFeatureResults("NX_Q9UI33-3"), 1120, 1120, MappedIsoformFeatureResult.Status.MAPPED);
        */
    }

    @Test
    public void shouldNotValidateIncompatibleProteinAndGeneName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_P01308");
        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.IncompatibleGeneAndProteinName("SCN11A", "NX_P01308"));
    }

    @Test
    public void shouldNotValidateInvalidVariantName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-z.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidVariantName("SCN11A-z.Leu1158Pro"));
    }

    @Test
    public void shouldNotValidateInvalidAminoAcidCode() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Let1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidVariantName("SCN11A-p.Let1158Pro"));
    }

    @Test
    public void shouldNotValidateIncorrectAAFeatureIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Met1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.UnexpectedAminoAcids("L", "M"));
    }

    @Test
    public void shouldNotValidateInvalidPositionFeatureIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158999Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidPosition("NX_Q9UI33-1", 1158999));
    }

    @Test
    public void shouldPropagateVariantToAllIsoforms() throws Exception {

        MappedIsoformsFeatureResult result = service.propagateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1120, 1120, true);
    }

    @Test
    public void shouldPropagateVariantToAllValidIsoforms() throws Exception {

        MappedIsoformsFeatureResult result = service.propagateFeature("SCN11A-p.Lys1710Thr", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1710, 1710, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", null, null, false);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1672, 1672, true);
    }

    @Ignore
    @Test
    public void testVDList() throws Exception {

        Assert.fail("todo");
    }

    private static void assertIsoformFeatureValid(MappedIsoformsFeatureResult result, String isoformName, Integer expectedFirstPos, Integer expectedLastPos, boolean mapped) {

        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(result instanceof MappedIsoformsFeatureSuccess);
        MappedIsoformsFeatureSuccess successResult = (MappedIsoformsFeatureSuccess) result;

        Assert.assertEquals(mapped, successResult.getMappedIsoformFeatureResult(isoformName).isMapped());
        Assert.assertEquals(expectedFirstPos, successResult.getMappedIsoformFeatureResult(isoformName).getFirstIsoSeqPos());
        Assert.assertEquals(expectedLastPos, successResult.getMappedIsoformFeatureResult(isoformName).getLastIsoSeqPos());
    }

    private static void assertIsoformFeatureNotValid(MappedIsoformsFeatureResult result, MappedIsoformsFeatureError.ErrorValue expected) {

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result instanceof MappedIsoformsFeatureError);
        MappedIsoformsFeatureError errorResult = (MappedIsoformsFeatureError) result;
    }
}