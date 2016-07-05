package com.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class MappedIsoformsFeatureSuccessTest {

    @Test
    public void testOnSuccess() {

        Query query =
                new Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureSuccess result = new MappedIsoformsFeatureSuccess(query);
        result.addMappedIsoformFeature("NX_Q9UI33-1", 1158, 1158);
        result.addMappedIsoformFeature("NX_Q9UI33-2", 1158, 1158);
        result.addMappedIsoformFeature("NX_Q9UI33-3", 1120, 1120);
    }
}