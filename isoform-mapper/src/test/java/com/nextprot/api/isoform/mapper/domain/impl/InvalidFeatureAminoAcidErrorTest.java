package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureFailure;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;

public class InvalidFeatureAminoAcidErrorTest {

    @Test
    public void testOnUnexpectedAminoAcidsError() {

        Query query =
                new Query("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), true);

        MappedIsoformsFeatureFailure result = new InvalidFeatureAminoAcidFailure(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.Alanine), AminoAcidCode.asArray(AminoAcidCode.Leucine));

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature specification: found amino-acid Ala at position 1158 of sequence isoform NX_Q9UI33 instead of Leu as incorrectly specified in feature 'SCN11A-p.Leu1158Pro'", result.getError().getMessage());
    }
}