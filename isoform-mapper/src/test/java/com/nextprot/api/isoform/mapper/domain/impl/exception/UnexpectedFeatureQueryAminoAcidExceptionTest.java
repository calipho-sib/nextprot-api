package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class UnexpectedFeatureQueryAminoAcidExceptionTest {

    @Test
    public void testOnUnexpectedAminoAcidsError() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        UnexpectedFeatureQueryAminoAcidException result = new UnexpectedFeatureQueryAminoAcidException(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.ALANINE), AminoAcidCode.asArray(AminoAcidCode.LEUCINE));

        Assert.assertEquals("Ala", result.getError().getCause("expectedAminoAcids"));
        Assert.assertEquals("Leu", result.getError().getCause("featureAminoAcids"));
        Assert.assertEquals(1158, result.getError().getCause("sequencePosition"));
    }
}