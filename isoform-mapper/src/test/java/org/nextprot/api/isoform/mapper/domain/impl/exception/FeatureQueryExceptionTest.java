package org.nextprot.api.isoform.mapper.domain.impl.exception;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.feature.impl.SequenceVariantTest;

import java.text.ParseException;
import java.util.Arrays;

public class FeatureQueryExceptionTest {

    @Test
    public void testEntryAccessionNotFoundForGeneException() throws FeatureQueryException {

        EntryAccessionNotFoundForGeneException featureQueryException = new EntryAccessionNotFoundForGeneException(Mockito.mock(SingleFeatureQuery.class), "SCN11A");
        Assert.assertEquals("cannot find entry accession for gene SCN11A", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testIncompatibleGeneAndProteinNameException() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P01308");

        IncompatibleGeneAndProteinNameException featureQueryException = new IncompatibleGeneAndProteinNameException(query, "SCN11A", Lists.newArrayList("INS"));

        Assert.assertEquals("gene->protein incompatibility: protein NX_P01308 is not compatible with gene SCN11A (expected genes: [INS])", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testInvalidFeatureQueryFormatException() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P01308");

        InvalidFeatureQueryFormatException featureQueryException = new InvalidFeatureQueryFormatException(query, Mockito.mock(ParseException.class));

        Assert.assertEquals("invalid feature format: SCN11A-p.Leu1158Pro", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testOutOfBoundSequencePositionException() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        OutOfBoundSequencePositionException featureQueryException = new OutOfBoundSequencePositionException(query, 23);
        Assert.assertEquals("out of bound sequence position: position 23 of NX_Q9UI33 sequence", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testUndefinedFQException() throws FeatureQueryException {

        UndefinedFeatureQueryException featureQueryException = new UndefinedFeatureQueryException(Mockito.mock(SingleFeatureQuery.class));
        Assert.assertEquals("undefined feature", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testUnexpectedAminoAcidsError() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        UnexpectedFeatureQueryAminoAcidException featureQueryException = new UnexpectedFeatureQueryAminoAcidException(query, 1158,
        new AminoAcidCode[] { AminoAcidCode.ALANINE }, new AminoAcidCode[] { AminoAcidCode.LEUCINE});

        Assert.assertEquals("Ala", featureQueryException.getReason().getCause("expectedAminoAcids"));
        Assert.assertEquals("Leu", featureQueryException.getReason().getCause("featureAminoAcids"));
        Assert.assertEquals(1158, featureQueryException.getReason().getCause("sequencePosition"));

        Assert.assertEquals("unexpected amino-acid: found Leu at position 1158 of NX_Q9UI33 sequence instead of expected Ala", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testUnknownFeatureIsoformException() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        UnknownFeatureIsoformException featureQueryException = new UnknownFeatureIsoformException(
                SequenceVariantTest.mockEntry("NX_Q9UI33",
                        SequenceVariantTest.mockIsoform("NX_Q9UI33-1", "Iso 1", true),
                        SequenceVariantTest.mockIsoform("NX_Q9UI33-1", "Iso 2", false),
                        SequenceVariantTest.mockIsoform("NX_Q9UI33-1", "Iso 3", false)),
                query,
                "spongebob");

        Assert.assertEquals("unknown isoform: cannot find isoform spongebob in entry NX_Q9UI33 (existing isoforms: [Iso 1, Iso 2, Iso 3])", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testUnknownFeatureQueryTypeException() throws FeatureQueryException {

        SingleFeatureQuery query =
                new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.CATALYTIC_ACTIVITY.getApiTypeName(), "NX_Q9UI33");

        UnknownFeatureQueryTypeException featureQueryException = new UnknownFeatureQueryTypeException(query);
        Assert.assertEquals("unknown feature type: cannot find feature type CatalyticActivity", featureQueryException.getReason().getMessage());
    }

    @Test
    public void testMultipleEntryAccessionForGeneException() throws FeatureQueryException {

        MultipleEntryAccessionForGeneException featureQueryException = new MultipleEntryAccessionForGeneException(Mockito.mock(SingleFeatureQuery.class), "roudoudou", Arrays.asList("NX_ROUDOUDOU-1", "NX_ROUDOUDOU-2"));
        Assert.assertEquals("multiple accessions: too many entry accessions found for gene roudoudou: [NX_ROUDOUDOU-1, NX_ROUDOUDOU-2]", featureQueryException.getReason().getMessage());
    }
}