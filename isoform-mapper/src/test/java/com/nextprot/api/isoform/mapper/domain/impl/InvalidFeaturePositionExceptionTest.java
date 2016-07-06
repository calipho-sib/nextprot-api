package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class InvalidFeaturePositionExceptionTest {

    @Test
    public void testOnInvalidPositionError() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), true);

        InvalidFeatureQueryPositionException result = new InvalidFeatureQueryPositionException(query, 23);
        Assert.assertEquals("invalid feature position: position 23 is out of bound in sequence of isoform NX_Q9UI33", result.getError().getMessage());
    }
}