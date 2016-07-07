package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.*;
import org.nextprot.api.commons.bio.variation.seq.format.AbstractProteinSequenceVariationFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProteinSequenceVariationHGVSParseSubstitutionTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();


    @Test(expected = ParseException.class)
    public void testParseUnknownCode1AA() throws Exception {

        format.parse("p.B54C");
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode3AA() throws Exception {

        format.parse("p.Mat54Trp");
    }

    ///// SUBSTITUTIONS
    @Test
    public void testParseSubstitution() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionStop() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54*");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Stop, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54C", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.*104E", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Ter104Glu", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getProteinSequenceChange().getValue());
    }

    @Ignore
    @Test
    public void testParseAATerSubstitutionFix5() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Y553_K558>", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Ignore
    @Test
    public void parserShouldBeAbleToParseGaussVariantsFromBED() throws IOException {

        String filename = getClass().getResource("gauss_variants.tsv").getFile();

        Map<String, VariantTypeReport> report = collectVariantParsingReportFromBED(filename);

        Assert.assertEquals(0, VariantTypeReport.countParsingErrors(report));
    }

    @Ignore
    @Test
    public void parserShouldBeAbleToParseStraussVariantsFromBED() throws IOException {

        String filename = getClass().getResource("strauss_variants.tsv").getFile();

        Map<String, VariantTypeReport> report = collectVariantParsingReportFromBED(filename);

        Assert.assertEquals(0, VariantTypeReport.countParsingErrors(report));
    }

    @Ignore
    @Test
    public void parserShouldBeAbleToParseVariantsFromBED() throws IOException {

        parseVariants(getClass().getResource("variants.tsv").getFile());
    }

    @Test
    public void testParseMultisVariants() {
        /*
MULTIS:
p.(=,Ile411_Gly426del)
         */
    }

    private static String[] trimQuotes(String... strs) {

        String[] dest = new String[strs.length];

        for (int i=0 ; i<strs.length ; i++) {
            dest[i] = strs[i].substring(1, strs[i].length() - 1);
        }

        return dest;
    }

    private static Map<String, VariantTypeReport> collectVariantParsingReportFromBED(String filename) throws IOException {

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        Map<String, String[]> variants = new HashMap<>();
        br.readLine();

        Map<String, VariantTypeReport> variantReport = new HashMap<>();
        HashMap<String, AtomicInteger> atomicCounter = new HashMap<>();

        while ( (line = br.readLine()) != null) {

            String[] fields = trimQuotes(line.split("\\t+"));

            String key = fields[0];
            variants.put(key, fields);

            String type = variants.get(key)[5];

            AtomicInteger value = atomicCounter.get(type);
            if (value != null)
                value.incrementAndGet();
            else
                atomicCounter.put(type, new AtomicInteger(1));
        }

        for (String variant : variants.keySet()) {

            String[] fields = variants.get(variant);

            String type = fields[5];
            String status = fields[18];

            if (!variant.contains("-")) {
                VariantTypeReport.populateMap(variantReport, type, atomicCounter.get(type).get(), variant + ": missing '-'", status);
            }
            else {
                try {
                    int p = variant.lastIndexOf("-");
                    String hgvMutation = variant.substring(p + 1);

                    if (!format.isValidProteinSequenceVariant(hgvMutation)) {
                        VariantTypeReport.populateMap(variantReport, type, atomicCounter.get(type).get(), variant + ": invalid format ('p.' expected)", status);
                    } else {
                        ProteinSequenceVariation mutation = format.parse(hgvMutation, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);
                        Assert.assertNotNull(mutation);
                    }
                } catch (ParseException e) {
                    VariantTypeReport.populateMap(variantReport, type, atomicCounter.get(type).get(), variant + ": " + e.getMessage(), status);
                }
            }
        }

        return variantReport;
    }

    private static void parseVariants(String filename) throws IOException {

        ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

        BufferedReader br = new BufferedReader(new FileReader(filename));
        String variant;

        int parsingErrorCount = 0;
        int variantCount = 0;
        while ( (variant = br.readLine()) != null) {

            if (!variant.contains("-")) {
                System.err.println("missing colon in "+variant);
            }
            else {

                int p = variant.lastIndexOf("-");
                String hgvMutation = variant.substring(p + 1);

                try {
                    ProteinSequenceVariation mutation = format.parse(hgvMutation, AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);
                    //System.out.println(hgvMutation + ": {" +mutation+"}");

                } catch (Exception e) {
                    parsingErrorCount++;
                    System.err.println(hgvMutation + ": {" +e.getMessage()+"}");
                }
            }

            variantCount++;
        }

        System.out.println("parsing error: "+parsingErrorCount+"/"+variantCount);
    }

    private static class VariantTypeReport {

        private final String type;
        private final List<String> parsingErrorMessages;
        private final int totalVariantCount;

        public VariantTypeReport(String type, int totalVariantCount) {
            this.type = type;
            this.totalVariantCount = totalVariantCount;
            this.parsingErrorMessages = new ArrayList<>();
        }

        public static void populateMap(Map<String, VariantTypeReport> variantReport, String type, int totalCount, String message, String status) {

            if (!variantReport.containsKey(type)) variantReport.put(type, new VariantTypeReport(type, totalCount));
            variantReport.get(type).addParsingErrorMessage(message+" (status="+status+")");
        }

        public static int countParsingErrors(Map<String, VariantTypeReport> variantReport) {

            int count=0;
            for (VariantTypeReport report : variantReport.values()) {
                count += report.countErrors();
            }
            return count;
        }

        public void addParsingErrorMessage(String message) {

            parsingErrorMessages.add(message);
        }

        public String getType() {
            return type;
        }

        public List<String> getParsingErrorMessages() {
            return parsingErrorMessages;
        }

        public int countErrors() {
            return parsingErrorMessages.size();
        }

        public int getTotalVariantCount() {
            return totalVariantCount;
        }

        @Override
        public String toString() {
            return "Report{" +
                    "type='" + type + '\'' +
                    ", total=" + totalVariantCount +
                    '}';
        }
    }

}