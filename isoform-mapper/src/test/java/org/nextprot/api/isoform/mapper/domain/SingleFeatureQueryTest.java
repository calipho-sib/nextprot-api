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
        Assert.assertTrue(!query.tryToMapOnOtherIsoforms());
    }

    @Test
    public void testPTMQuery() throws FeatureQueryException {

        SingleFeatureQuery query = new SingleFeatureQuery("NX_Q06187.PTM-0253_21", AnnotationCategory.GENERIC_PTM.getApiTypeName(), "NX_Q06187");

        Assert.assertEquals("NX_Q06187", query.getAccession());
        Assert.assertEquals("NX_Q06187.PTM-0253_21", query.getFeature());
        Assert.assertEquals("Ptm", query.getFeatureType());
        Assert.assertTrue(!query.tryToMapOnOtherIsoforms());
    }
}