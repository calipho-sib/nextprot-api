package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;

import static com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccessTest.mockEntry;

public class InvalidFeatureAminoAcidExceptionTest {

    @Test
    public void testOnUnexpectedAminoAcidsError() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery(mockEntry("NX_Q9UI33"), "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        InvalidFeatureQueryAminoAcidException result = new InvalidFeatureQueryAminoAcidException(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.ALANINE), AminoAcidCode.asArray(AminoAcidCode.LEUCINE));

        Assert.assertEquals("invalid feature specification: found amino-acid Ala at position 1158 of sequence isoform NX_Q9UI33 instead of Leu as incorrectly specified in feature 'SCN11A-p.Leu1158Pro'", result.getError().getMessage());
    }
}