package com.nextprot.api.isoform.mapper.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

import static com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccessTest.mockEntryIsoform;

public class FeatureQueryTest {

    @Test
    public void testStandardQuery() throws FeatureQueryException {

        FeatureQuery query = new FeatureQuery(mockEntryIsoform("NX_Q9UI33", "NX_Q9UI33-1", "Iso 1"), "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        Assert.assertEquals("NX_Q9UI33", query.getAccession());
        Assert.assertEquals("SCN11A-p.Leu1158Pro", query.getFeature());
        Assert.assertEquals("Variant", query.getFeatureType());
        Assert.assertTrue(!query.isFeaturePropagable());
    }
}