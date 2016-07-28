package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.google.common.collect.Lists;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class IncompatibleGeneAndProteinNameExceptionTest {

    @Test
    public void testOnIncompatibleProteinAndGeneNameError() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_P01308", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        IncompatibleGeneAndProteinNameException result = new IncompatibleGeneAndProteinNameException(query, "SCN11A", Lists.newArrayList("INS"));

        Assert.assertEquals("gene/protein incompatibility: protein NX_P01308 is not compatible with gene SCN11A (expected genes: [INS])", result.getError().getMessage());
    }
}