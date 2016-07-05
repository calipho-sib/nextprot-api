package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.collect.Lists;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class IncompatibleGeneAndProteinNameTest {

    @Test
    public void testOnIncompatibleProteinAndGeneNameError() {

        Query query =
                new Query("NX_P01308", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, true);

        MappedIsoformsFeatureError result = new IncompatibleGeneAndProteinName(query, "SCN11A", Lists.newArrayList("INS"));

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("gene/protein incompatibility: protein NX_P01308 is not compatible with gene SCN11A (expected genes: [INS])", result.getError().getMessage());
    }
}