package com.nextprot.api.isoform.mapper.service;

import com.google.common.collect.Lists;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureSuccess;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.when;

@ActiveProfiles({ "dev", "cache" })
public class IsoformMappingServiceTest extends IsoformMappingBaseTest {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    private IsoformMappingService service;

    @Test
    public void shouldValidateVariantOnCanonicalIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
    }

    @Test
    public void shouldNotValidateIncompatibleProteinAndGeneName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_P01308");

        MappedIsoformsFeatureResult.Query query = Mockito.mock(MappedIsoformsFeatureResult.Query.class);
        when(query.getAccession()).thenReturn("NX_P01308");

        assertIsoformFeatureNotValid((MappedIsoformsFeatureError) result, new MappedIsoformsFeatureError.IncompatibleGeneAndProteinName(query, "SCN11A", Lists.newArrayList("INS")));
    }

    @Test
    public void shouldNotValidateInvalidVariantName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-z.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        MappedIsoformsFeatureResult.Query query = Mockito.mock(MappedIsoformsFeatureResult.Query.class);
        when(query.getFeature()).thenReturn("SCN11A-z.Leu1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-z.Leu1158Pro", ((MappedIsoformsFeatureError)result).getError().getMessage());
        Assert.assertEquals(2, ((MappedIsoformsFeatureError)result).getError().getCauses().size());
        Assert.assertEquals("z.Leu1158Pro: not a valid protein sequence variant", ((MappedIsoformsFeatureError.InvalidFeatureFormat)result).getError().getCause(MappedIsoformsFeatureError.InvalidFeatureFormat.PARSE_ERROR_MESSAGE));
        Assert.assertEquals(7, ((MappedIsoformsFeatureError.InvalidFeatureFormat)result).getError().getCause(MappedIsoformsFeatureError.InvalidFeatureFormat.PARSE_ERROR_OFFSET));
    }

    @Test
    public void shouldNotValidateInvalidAminoAcidCode() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Let1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        MappedIsoformsFeatureResult.Query query = Mockito.mock(MappedIsoformsFeatureResult.Query.class);
        when(query.getFeature()).thenReturn("SCN11A-p.Let1158Pro");

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("invalid feature format: SCN11A-p.Let1158Pro", ((MappedIsoformsFeatureError)result).getError().getMessage());
        Assert.assertEquals(2, ((MappedIsoformsFeatureError)result).getError().getCauses().size());
        Assert.assertEquals("Let: invalid AminoAcidCode", ((MappedIsoformsFeatureError.InvalidFeatureFormat)result).getError().getCause(MappedIsoformsFeatureError.InvalidFeatureFormat.PARSE_ERROR_MESSAGE));
        Assert.assertEquals(9, ((MappedIsoformsFeatureError.InvalidFeatureFormat)result).getError().getCause(MappedIsoformsFeatureError.InvalidFeatureFormat.PARSE_ERROR_OFFSET));
    }

    @Test
    public void shouldNotValidateIncorrectAAVariantIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Met1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        MappedIsoformsFeatureResult.Query query = Mockito.mock(MappedIsoformsFeatureResult.Query.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");
        when(query.getFeature()).thenReturn("SCN11A-p.Met1158Pro");

        assertIsoformFeatureNotValid((MappedIsoformsFeatureError) result, new MappedIsoformsFeatureError.InvalidFeatureAminoAcid(query, 1158,
                AminoAcidCode.asArray(AminoAcidCode.Leucine), AminoAcidCode.asArray(AminoAcidCode.Methionine)));
    }

    @Test
    public void shouldNotValidateInvalidPositionVariantIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158999Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        MappedIsoformsFeatureResult.Query query = Mockito.mock(MappedIsoformsFeatureResult.Query.class);
        when(query.getAccession()).thenReturn("NX_Q9UI33");

        assertIsoformFeatureNotValid((MappedIsoformsFeatureError) result, new MappedIsoformsFeatureError.InvalidFeaturePosition(query, 1158999));
    }

    @Test
    public void shouldPropagateVariantToAllIsoforms() throws Exception {

        MappedIsoformsFeatureResult result = service.propagateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", 1158, 1158, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1120, 1120, true);
    }

    @Test
    public void shouldPropagateVariantToAllValidIsoforms() throws Exception {

        MappedIsoformsFeatureResult result = service.propagateFeature("SCN11A-p.Lys1710Thr", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1710, 1710, true);
        assertIsoformFeatureValid(result, "NX_Q9UI33-2", null, null, false);
        assertIsoformFeatureValid(result, "NX_Q9UI33-3", 1672, 1672, true);
    }

    @Test
    public void shouldValidatePtmOnCanonicalIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("BRCA1-P-Ser988", AnnotationCategory.PTM_INFO, "NX_P38398");

        assertIsoformFeatureValid(result, "NX_P38398-1", 988, 988, true);
    }

    //@Test
    public void validateVDList() throws Exception {

        FileInputStream is = new FileInputStream(IsoformMappingServiceTest.class.getResource("vd.tsv").getFile());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter("vd-results.tsv");

        List<String[]> twoFirstFieldsList = br.lines()
                .map(to2FirstFields)
                .collect(toList());

        pw.append("accession\tvariant\tvalid\terror message\n");
        for (String[] twoFields : twoFirstFieldsList) {

            String accession = twoFields[0];
            String feature = twoFields[1];

            MappedIsoformsFeatureResult result =
                    service.validateFeature(feature, AnnotationCategory.VARIANT, accession);

            pw.append(accession).append("\t").append(feature).append("\t").append(String.valueOf(result.isSuccess()));

            if (result.isSuccess()) {
                pw.append("\t");
            } else {
                MappedIsoformsFeatureError error = (MappedIsoformsFeatureError) result;
                pw.append("\t").append(error.getError().getMessage());
            }
            pw.append("\n");
        }
        pw.close();
    }

    private static Function<String, String[]> to2FirstFields = (line) -> {
        String[] p = line.split("\t");
        return new String[] { p[0], p[1] };
    };

    private static void assertIsoformFeatureValid(MappedIsoformsFeatureResult result, String isoformName, Integer expectedFirstPos, Integer expectedLastPos, boolean mapped) {

        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(result instanceof MappedIsoformsFeatureSuccess);
        MappedIsoformsFeatureSuccess successResult = (MappedIsoformsFeatureSuccess) result;

        Assert.assertEquals(mapped, successResult.getMappedIsoformFeatureResult(isoformName).isMapped());
        Assert.assertEquals(expectedFirstPos, successResult.getMappedIsoformFeatureResult(isoformName).getFirstIsoSeqPos());
        Assert.assertEquals(expectedLastPos, successResult.getMappedIsoformFeatureResult(isoformName).getLastIsoSeqPos());
    }

    private static void assertIsoformFeatureNotValid(MappedIsoformsFeatureError result, MappedIsoformsFeatureError expected) {

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(expected.getError(), result.getError());
    }

    private static ParseException mockParseException(String mess) {

        ParseException exception = Mockito.mock(ParseException.class);
        when(exception.getMessage()).thenReturn(mess);
        return exception;
    }
}