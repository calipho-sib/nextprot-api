package org.nextprot.api.isoform.mapper.domain.impl.exception;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.EntryUtilsTest;
import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.EntryAccessionNotFoundForGeneException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.MultipleEntryAccessionForGeneException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.OutOfBoundSequencePositionException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UndefinedFeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnexpectedFeatureQueryAminoAcidException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureIsoformException;
import org.nextprot.api.isoform.mapper.domain.impl.exception.UnknownFeatureQueryTypeException;

import java.text.ParseException;
import java.util.Arrays;

public class FeatureQueryExceptionTest {

    @Test
    public void testEntryAccessionNotFoundForGeneException() throws FeatureQueryException {

        EntryAccessionNotFoundForGeneException featureQueryException = new EntryAccessionNotFoundForGeneException(Mockito.mock(FeatureQuery.class), "SCN11A");
        Assert.assertEquals("cannot find entry accession for gene SCN11A", featureQueryException.getError().getMessage());
    }

    @Test
    public void testIncompatibleGeneAndProteinNameException() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_P01308", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        IncompatibleGeneAndProteinNameException featureQueryException = new IncompatibleGeneAndProteinNameException(query, "SCN11A", Lists.newArrayList("INS"));

        Assert.assertEquals("gene->protein incompatibility: protein NX_P01308 is not compatible with gene SCN11A (expected genes: [INS])", featureQueryException.getError().getMessage());
    }

    @Test
    public void testInvalidFeatureQueryFormatException() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_P01308", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        InvalidFeatureQueryFormatException featureQueryException = new InvalidFeatureQueryFormatException(query, Mockito.mock(ParseException.class));

        Assert.assertEquals("invalid feature format: SCN11A-p.Leu1158Pro", featureQueryException.getError().getMessage());
    }

    @Test
    public void testOutOfBoundSequencePositionException() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        OutOfBoundSequencePositionException featureQueryException = new OutOfBoundSequencePositionException(query, 23);
        Assert.assertEquals("out of bound sequence position: position 23 of NX_Q9UI33 sequence", featureQueryException.getError().getMessage());
    }

    @Test
    public void testUndefinedFQException() throws FeatureQueryException {

        UndefinedFeatureQueryException featureQueryException = new UndefinedFeatureQueryException(Mockito.mock(FeatureQuery.class));
        Assert.assertEquals("undefined feature", featureQueryException.getError().getMessage());
    }

    @Test
    public void testUnexpectedAminoAcidsError() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        UnexpectedFeatureQueryAminoAcidException featureQueryException = new UnexpectedFeatureQueryAminoAcidException(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.ALANINE), AminoAcidCode.asArray(AminoAcidCode.LEUCINE));

        Assert.assertEquals("Ala", featureQueryException.getError().getCause("expectedAminoAcids"));
        Assert.assertEquals("Leu", featureQueryException.getError().getCause("featureAminoAcids"));
        Assert.assertEquals(1158, featureQueryException.getError().getCause("sequencePosition"));

        Assert.assertEquals("unexpected amino-acid: found Leu at position 1158 of NX_Q9UI33 sequence instead of expected Ala", featureQueryException.getError().getMessage());
    }

    @Test
    public void testUnknownFeatureIsoformException() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        UnknownFeatureIsoformException featureQueryException = new UnknownFeatureIsoformException(
                EntryUtilsTest.mockEntry("NX_Q9UI33",
                        EntryUtilsTest.mockIsoform("NX_Q9UI33-1", "Iso 1", true),
                        EntryUtilsTest.mockIsoform("NX_Q9UI33-1", "Iso 2", false),
                        EntryUtilsTest.mockIsoform("NX_Q9UI33-1", "Iso 3", false)),
                query,
                "spongebob");

        Assert.assertEquals("unknown isoform: cannot find isoform spongebob in entry NX_Q9UI33 (existing isoforms: [Iso 1, Iso 2, Iso 3])", featureQueryException.getError().getMessage());
    }

    @Test
    public void testUnknownFeatureQueryTypeException() throws FeatureQueryException {

        FeatureQuery query =
                new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.CATALYTIC_ACTIVITY.getApiTypeName());

        UnknownFeatureQueryTypeException featureQueryException = new UnknownFeatureQueryTypeException(query);
        Assert.assertEquals("unknown feature type: cannot find feature type CatalyticActivity", featureQueryException.getError().getMessage());
    }

    @Test
    public void testMultipleEntryAccessionForGeneException() throws FeatureQueryException {

        MultipleEntryAccessionForGeneException featureQueryException = new MultipleEntryAccessionForGeneException(Mockito.mock(FeatureQuery.class), "roudoudou", Arrays.asList("NX_ROUDOUDOU-1", "NX_ROUDOUDOU-2"));
        Assert.assertEquals("multiple accessions: too many entry accessions found for gene roudoudou: [NX_ROUDOUDOU-1, NX_ROUDOUDOU-2]", featureQueryException.getError().getMessage());
    }
}