package org.nextprot.api.isoform.mapper.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailureImpl;
import org.nextprot.api.isoform.mapper.domain.impl.SingleFeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;

@ActiveProfiles({ "dev" })
public class IsoformMappingServiceTest extends IsoformMappingBaseTest {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    private IsoformMappingService service;

    @Test
    public void shouldValidateVariantOnCanonicalIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
    }

    @Test
    public void shouldNotValidateIncompatibleProteinAndGeneName() throws Exception {

        FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P01308"));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_P01308");

        assertIsoformFeatureNotValid((FeatureQueryFailureImpl) result, new IncompatibleGeneAndProteinNameException(query, "SCN11A", Lists.newArrayList("INS")));
    }

    @Test
    public void shouldNotValidateInvalidVariantName() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-z.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getFeature()).thenReturn("SCN11A-z.Leu1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-z.Leu1158Pro", ((FeatureQueryFailureImpl)result).getError().getMessage());
        Assert.assertEquals(1, ((FeatureQueryFailureImpl)result).getError().getCauses().size());
        Assert.assertEquals("z.Leu1158Pro: not a valid protein sequence variant", ((FeatureQueryFailureImpl)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_MESSAGE));
    }

    @Test
    public void shouldNotValidateInvalidAminoAcidCode() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Let1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getFeature()).thenReturn("SCN11A-p.Let1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-p.Let1158Pro", ((FeatureQueryFailureImpl)result).getError().getMessage());
        Assert.assertEquals(1, ((FeatureQueryFailureImpl)result).getError().getCauses().size());
        Assert.assertEquals("Let: invalid AminoAcidCode", ((FeatureQueryFailureImpl)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_MESSAGE));
    }

    @Test
    public void shouldNotValidateIncorrectAAVariantIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Met1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");
        when(query.getFeature()).thenReturn("SCN11A-p.Met1158Pro");

        assertIsoformFeatureNotValid((FeatureQueryFailure) result, new UnexpectedFeatureQueryAminoAcidException(query, 1158,
                new AminoAcidCode[] { AminoAcidCode.LEUCINE }, new AminoAcidCode[] { AminoAcidCode.METHIONINE }));
    }

    @Test
    public void shouldNotValidateInvalidPositionVariantIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158999Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");

        assertIsoformFeatureNotValid((FeatureQueryFailureImpl) result, new OutOfBoundSequencePositionException(query, 1158999));
    }

    @Test
    public void shouldPropagateVariantToAllIsoforms() throws Exception {

    	FeatureQueryResult result = service.propagateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1120, 1120, true);
    }

    @Test
    public void shouldPropagateVariantToAllValidIsoforms() throws Exception {

    	FeatureQueryResult result = service.propagateFeature(new SingleFeatureQuery("SCN11A-p.Lys1710Thr", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1710, 1710, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", null, null, false);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1672, 1672, true);
    }

    @Test
    public void shouldValidatePtmOnCanonicalIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("BRCA1-P-Ser988", AnnotationCategory.GENERIC_PTM.getApiTypeName(), "NX_P38398"));

        assertIsoformFeatureValid(result, "NX_P38398-1", 988, 988, true);
    }

    @Test
    public void shouldValidateInsertionVariantOnCanonicalIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("MLH1-p.Lys722_Ala723insTyrLys", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P40692"));

        assertIsoformFeatureValid(result, "NX_P40692-1", 722, 723, true);
    }

    @Test
    public void shouldValidateDeletionVariantOnCanonicalIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("BRCA2-p.Gly2281_Asp2312del", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P51587"));

        assertIsoformFeatureValid(result, "NX_P51587-1", 2281, 2312, true);
    }

    //@Test
    public void validateVDList1() throws Exception {

        String filename = IsoformMappingServiceTest.class.getResource("vd.tsv").getFile();

        validateList(filename, true, service);
    }

    //@Test
    public void validateVDList2() throws Exception {

        String filename = IsoformMappingServiceTest.class.getResource("variant-multiple-mutants.csv").getFile();

        validateList(filename, false, service);
    }

    @Test(expected = NextProtException.class)
    public void shouldThrowExceptionWithIsonumber() throws Exception {

        service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33-2"));
    }

    @Test
    public void shouldReturnError() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p-iso4.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33"));

        Assert.assertTrue(!result.isSuccess());
    }

    @Test
    public void shouldValidateWithNoAccession() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), ""));

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
    }

    @Test
    public void shouldValidateWithNullAccession() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), null));

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        Assert.assertEquals("NX_Q9UI33", result.getQuery().getAccession());
    }

    @Test
    public void shouldNotValidateWithGeneNoAccession() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("SCN14A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), ""));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getAccession()).thenReturn("");

        assertIsoformFeatureNotValid((FeatureQueryFailureImpl) result, new EntryAccessionNotFoundForGeneException(query, "SCN14A"));
    }

    // no more multiple accessions for gene GCNT2
    // TODO: find another gene with multiple accessions
    @Ignore
    @Test
    public void shouldNotValidateWithMultipleAccessions() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("GCNT2-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), ""));

        SingleFeatureQuery query = Mockito.mock(SingleFeatureQuery.class);
        when(query.getAccession()).thenReturn("");

        assertIsoformFeatureNotValid((FeatureQueryFailureImpl) result, new MultipleEntryAccessionForGeneException(query, "GCNT2",
                Sets.newHashSet("NX_Q06430", "NX_Q8N0V5", "NX_Q8NFS9")));
    }

    @Test
    public void shouldValidateMutagenesisOnCanonicalIsoform() throws Exception {

    	FeatureQueryResult result = service.validateFeature(new SingleFeatureQuery("ACVR1-p.Gln207Asp", AnnotationCategory.MUTAGENESIS.getApiTypeName(), ""));

        assertIsoformFeatureValid(result, "NX_Q04771-1", 207, 207, true);
    }

    private static void assertIsoformFeatureValid(FeatureQueryResult result, String featureIsoformName, Integer expectedFirstPos, Integer expectedLastPos, boolean mapped) {

        Assert.assertTrue(result.isSuccess());
        SingleFeatureQuerySuccessImpl successResult = (SingleFeatureQuerySuccessImpl) result;

        Assert.assertNotNull(successResult.getIsoformFeatureResult(featureIsoformName));
        Assert.assertEquals(mapped, successResult.getIsoformFeatureResult(featureIsoformName).isMapped());
        Assert.assertEquals(expectedFirstPos, successResult.getIsoformFeatureResult(featureIsoformName).getBeginIsoformPosition());
        Assert.assertEquals(expectedLastPos, successResult.getIsoformFeatureResult(featureIsoformName).getEndIsoformPosition());
    }

    private static void assertIsoformFeatureNotValid(FeatureQueryFailure result, FeatureQueryException expectedException) {

        Assert.assertTrue(!result.isSuccess());
        Assert.assertEquals(expectedException.getError(), result.getError());
    }

    private static void validateList(String filename, boolean tabSep, IsoformMappingService service) throws Exception {

        FileInputStream is = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(Files.getNameWithoutExtension(filename)+"-results.tsv");

        List<String[]> twoFirstFieldsList = br.lines()
                .map((tabSep) ? to2FirstTabFields : to2FirstCommaFields)
                .collect(toList());

        pw.append("accession\tvariant\tvalid\terror message\n");
        for (String[] twoFields : twoFirstFieldsList) {

            String accession = twoFields[0];
            String feature = twoFields[1];

            FeatureQueryResult result =
                    service.validateFeature(new SingleFeatureQuery(feature, AnnotationCategory.VARIANT.getApiTypeName(), accession));

            pw.append(accession).append("\t").append(feature).append("\t").append(String.valueOf(result.isSuccess()));

            if (result.isSuccess()) {
                pw.append("\t");
            } else {
                FeatureQueryFailureImpl error = (FeatureQueryFailureImpl) result;
                pw.append("\t").append(error.getError().getMessage());
            }
            pw.append("\n");
        }
        pw.close();
    }

    private static Function<String, String[]> to2FirstTabFields = (line) -> {
        String[] p = line.split("\t");
        return new String[] { p[0], p[1] };
    };

    private static Function<String, String[]> to2FirstCommaFields = (line) -> {
        String[] p = line.split(",");
        return new String[] { p[0], p[1] };
    };
}