package com.nextprot.api.isoform.mapper.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQueryFailure;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import com.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleGeneAndProteinNameException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryAminoAcidException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryFormatException;
import com.nextprot.api.isoform.mapper.domain.impl.exception.InvalidFeatureQueryPositionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.OverviewService;
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

        FeatureQueryResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
    }

    @Test
    public void shouldNotValidateIncompatibleProteinAndGeneName() throws Exception {

        FeatureQueryResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P01308");

        FeatureQuery query = Mockito.mock(FeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_P01308");

        assertIsoformFeatureNotValid((FeatureQueryFailure) result, new IncompatibleGeneAndProteinNameException(query, "SCN11A", Lists.newArrayList("INS")));
    }

    @Test
    public void shouldNotValidateInvalidVariantName() throws Exception {

        FeatureQueryResult result = service.validateFeature("SCN11A-z.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        FeatureQuery query = Mockito.mock(FeatureQuery.class);
        when(query.getFeature()).thenReturn("SCN11A-z.Leu1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-z.Leu1158Pro", ((FeatureQueryFailure)result).getError().getMessage());
        Assert.assertEquals(2, ((FeatureQueryFailure)result).getError().getCauses().size());
        Assert.assertEquals("z.Leu1158Pro: not a valid protein sequence variant", ((FeatureQueryFailure)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_MESSAGE));
        Assert.assertEquals(7, ((FeatureQueryFailure)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_OFFSET));
    }

    @Test
    public void shouldNotValidateInvalidAminoAcidCode() throws Exception {

        FeatureQueryResult result = service.validateFeature("SCN11A-p.Let1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        FeatureQuery query = Mockito.mock(FeatureQuery.class);
        when(query.getFeature()).thenReturn("SCN11A-p.Let1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-p.Let1158Pro", ((FeatureQueryFailure)result).getError().getMessage());
        Assert.assertEquals(2, ((FeatureQueryFailure)result).getError().getCauses().size());
        Assert.assertEquals("Let: invalid AminoAcidCode", ((FeatureQueryFailure)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_MESSAGE));
        Assert.assertEquals(9, ((FeatureQueryFailure)result).getError().getCause(InvalidFeatureQueryFormatException.PARSE_ERROR_OFFSET));
    }

    @Test
    public void shouldNotValidateIncorrectAAVariantIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature("SCN11A-p.Met1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        FeatureQuery query = Mockito.mock(FeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");
        when(query.getFeature()).thenReturn("SCN11A-p.Met1158Pro");

        assertIsoformFeatureNotValid((FeatureQueryFailure) result, new InvalidFeatureQueryAminoAcidException(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.LEUCINE), AminoAcidCode.asArray(AminoAcidCode.METHIONINE)));
    }

    @Test
    public void shouldNotValidateInvalidPositionVariantIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature("SCN11A-p.Leu1158999Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        FeatureQuery query = Mockito.mock(FeatureQuery.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");

        assertIsoformFeatureNotValid((FeatureQueryFailure) result, new InvalidFeatureQueryPositionException(query, 1158999));
    }

    @Test
    public void shouldPropagateVariantToAllIsoforms() throws Exception {

        FeatureQueryResult result = service.propagateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1120, 1120, true);
    }

    @Test
    public void shouldPropagateVariantToAllValidIsoforms() throws Exception {

        FeatureQueryResult result = service.propagateFeature("SCN11A-p.Lys1710Thr", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1710, 1710, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", null, null, false);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1672, 1672, true);
    }

    @Test
    public void shouldValidatePtmOnCanonicalIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature("BRCA1-P-Ser988", AnnotationCategory.GENERIC_PTM.getApiTypeName(), "NX_P38398");

        assertIsoformFeatureValid(result, "NX_P38398-1", 988, 988, true);
    }

    @Test
    public void shouldValidateInsertionVariantOnCanonicalIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature("MLH1-p.Lys722_Ala723insTyrLys", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P40692");

        assertIsoformFeatureValid(result, "NX_P40692-1", 722, 723, true);
    }

    @Test
    public void shouldValidateDeletionVariantOnCanonicalIsoform() throws Exception {

        FeatureQueryResult result = service.validateFeature("BRCA2-p.Gly2281_Asp2312del", AnnotationCategory.VARIANT.getApiTypeName(), "NX_P51587");

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

        service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), "NX_Q9UI33-2");
    }

    private static void assertIsoformFeatureValid(FeatureQueryResult result, String featureIsoformName, Integer expectedFirstPos, Integer expectedLastPos, boolean mapped) {

        Assert.assertTrue(result.isSuccess());
        FeatureQuerySuccess successResult = (FeatureQuerySuccess) result;

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
                    service.validateFeature(feature, AnnotationCategory.VARIANT.getApiTypeName(), accession);

            pw.append(accession).append("\t").append(feature).append("\t").append(String.valueOf(result.isSuccess()));

            if (result.isSuccess()) {
                pw.append("\t");
            } else {
                FeatureQueryFailure error = (FeatureQueryFailure) result;
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