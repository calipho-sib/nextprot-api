package com.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class QueryTest {

    @Test
    public void testStandardQuery() {

        Query query =
                new Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals("SCN11A-p.Leu1158Pro", query.getFeature());
        Assert.assertEquals("Variant", query.getFeatureType());
        Assert.assertTrue(query.isFeaturePropagable());
    }
}