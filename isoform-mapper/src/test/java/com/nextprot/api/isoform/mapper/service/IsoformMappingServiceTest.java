package com.nextprot.api.isoform.mapper.service;

import com.google.common.collect.Lists;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureResult;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureSuccess;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
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

@ActiveProfiles({ "dev", "cache" })
public class IsoformMappingServiceTest extends IsoformMappingBaseTest {

    @Autowired
    public OverviewService overviewService;

    @Autowired
    private IsoformMappingService service;

    @Test
    public void shouldValidateFeatureOnCanonicalIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureValid(result, "NX_Q9UI33-1", 1158, 1158, true);
    }

    @Test
    public void shouldNotValidateIncompatibleProteinAndGeneName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT, "NX_P01308");
        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.IncompatibleGeneAndProteinName("SCN11A", "NX_P01308", Lists.newArrayList("INS")));
    }

    @Test
    public void shouldNotValidateInvalidVariantName() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-z.Leu1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidFeatureFormat("SCN11A-z.Leu1158Pro"));
    }

    @Test
    public void shouldNotValidateInvalidAminoAcidCode() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Let1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidFeatureFormat("SCN11A-p.Let1158Pro"));
    }

    @Test
    public void shouldNotValidateIncorrectAAFeatureIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Met1158Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidFeatureAminoAcid("NX_Q9UI33", "L", "M"));
    }

    @Test
    public void shouldNotValidateInvalidPositionFeatureIsoform() throws Exception {

        MappedIsoformsFeatureResult result = service.validateFeature("SCN11A-p.Leu1158999Pro", AnnotationCategory.VARIANT, "NX_Q9UI33");

        assertIsoformFeatureNotValid(result, new MappedIsoformsFeatureError.InvalidFeaturePosition("NX_Q9UI33-1", 1158999));
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

    private static void assertIsoformFeatureNotValid(MappedIsoformsFeatureResult result, MappedIsoformsFeatureError.FeatureErrorValue expected) {

        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result instanceof MappedIsoformsFeatureError);
        MappedIsoformsFeatureError errorResult = (MappedIsoformsFeatureError) result;
    }
}