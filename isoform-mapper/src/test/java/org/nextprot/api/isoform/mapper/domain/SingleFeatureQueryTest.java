package org.nextprot.api.isoform.mapper.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class SingleFeatureQueryTest {

    @Test
    public void testStandardQuery() throws FeatureQueryException {

        SingleFeatureQuery query = new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals("SCN11A-p.Leu1158Pro", query.getFeature());
        Assert.assertEquals("Variant", query.getFeatureType());
        Assert.assertTrue(!query.isFeaturePropagable());
    }
}