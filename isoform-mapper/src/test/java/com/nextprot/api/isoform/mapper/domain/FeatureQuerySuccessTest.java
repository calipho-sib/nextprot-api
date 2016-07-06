package com.nextprot.api.isoform.mapper.domain;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class FeatureQuerySuccessTest {

    @Test
    public void testOnSuccess() throws FeatureQueryException {

        FeatureQuery query = new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), true);

        FeatureQuerySuccess result = new FeatureQuerySuccess(query);
        result.addMappedFeature("NX_Q9UI33-1", 1158, 1158);
        result.addMappedFeature("NX_Q9UI33-2", 1158, 1158);
        result.addMappedFeature("NX_Q9UI33-3", 1120, 1120);
    }
}