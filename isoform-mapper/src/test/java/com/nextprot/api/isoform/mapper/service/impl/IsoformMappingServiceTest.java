package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
import com.nextprot.api.isoform.mapper.domain.IsoformFeatureMapping;
import com.nextprot.api.isoform.mapper.service.IsoformMappingService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;

public class IsoformMappingServiceTest extends IsoformMappingBaseTest {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    private IsoformMappingService service;

    // http://cancer.sanger.ac.uk/cosmic/mutation/overview?id=4408659
    @Ignore
    @Test
    public void shouldPropagateVariantToAllValidIsoforms() throws Exception {

        IsoformFeatureMapping mapping = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33", true);

        Assert.assertEquals(3, mapping.getIsoformFeatureNumber());
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-1"), 1158, 1158, IsoformFeature.Status.MAPPED);
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-2"), 1158, 1158, IsoformFeature.Status.MAPPED);
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-3"), 1120, 1120, IsoformFeature.Status.MAPPED);
    }

    @Ignore
    @Test
    public void shouldPropagateVariantToAllValidIsoforms2() throws Exception {

        IsoformFeatureMapping mapping = service.validateFeature("SCN11A-p.Lys1710Thr", AnnotationCategory.VARIANT, "NX_Q9UI33", true);

        Assert.assertEquals(3, mapping.getIsoformFeatureNumber());
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-1"), 1710, 1710, IsoformFeature.Status.MAPPED);
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-2"), null, null, IsoformFeature.Status.UNMAPPED);
        assertIsoformFeature(mapping.getIsoformFeature("NX_Q9UI33-3"), 1672, 1672, IsoformFeature.Status.MAPPED);
    }

    @Ignore
    @Test(expected = NextProtException.class)
    public void shouldThrowNPExceptionIfAccessionIncompatibleWithGeneName() throws Exception {

        service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_P01308", true);
    }

    @Test
    public void shouldValidateAndNotPropagateVariantToAllValidIsoforms() throws Exception {

        IsoformFeatureMapping mapping = service.validateFeature("WT1-iso4-p.Phe154Ser", AnnotationCategory.VARIANT, "NX_P19544-4", false);

        Assert.assertEquals(1, mapping.getIsoformFeatureNumber());
        Assert.assertTrue(mapping.hasIsoformFeature("NX_P19544-4"));
        assertIsoformFeature(mapping.getIsoformFeature("NX_P19544-4"), null, null, IsoformFeature.Status.UNMAPPED);
    }

    private static void assertIsoformFeature(IsoformFeature feature, Integer expectedFirstPos, Integer expectedLastPos,
                                             IsoformFeature.Status expectedStatus) {

        Assert.assertEquals(expectedFirstPos, feature.getFirstPositionOnIsoform());
        Assert.assertEquals(expectedLastPos, feature.getLastPositionOnIsoform());
        Assert.assertEquals(expectedStatus, feature.getStatus());
    }
}