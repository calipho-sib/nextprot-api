package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class InvalidFeaturePositionTest {

    @Test
    public void testOnInvalidPositionError() {

        Query query =
                new Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new InvalidFeaturePosition(query, 23);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature position: position 23 is out of bound in sequence of isoform NX_Q9UI33", result.getError().getMessage());
    }
}