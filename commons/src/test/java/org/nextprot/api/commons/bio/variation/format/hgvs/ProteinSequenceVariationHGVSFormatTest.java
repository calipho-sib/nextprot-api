package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.*;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProteinSequenceVariationHGVSFormatTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testFormatSubstitution() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionWithStop() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.GlutamicAcid, 31).substitutedBy(AminoAcidCode.Stop).build();

        Assert.assertEquals("p.E31*", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.M682fs*1", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.Met682fsTer1", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

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
    public void testParseAADeletion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.M682fs*1");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399delinsL");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331delinsF");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420delinsSSDG");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
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
    public void testParseAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Met682fsTer1");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Thr399delinsLeu");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Leu330_Ala331delinsPhe");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Asp419_Arg420delinsSerSerAspGly");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
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
    public void testParseAANonStandardDeletion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498del12", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498delPRAL", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.S1476fs*>9", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Serine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1476, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(9, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399>L", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins2() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331>F", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.W39_E40>*", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Tryptophan, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getLastChangingAminoAcid());
        Assert.assertEquals(39, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(40, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("*"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins4() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420>SSDG", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
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

    @Test
    public void testFormatSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Stop, 104).substitutedBy(AminoAcidCode.GlutamicAcid).build();

        Assert.assertEquals("p.*104E", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Stop, 104).substitutedBy(AminoAcidCode.GlutamicAcid).build();

        Assert.assertEquals("p.Ter104Glu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testParseInsertion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.C136_A137insGM", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(136, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(137, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("GM"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testFormatInsertionCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 136, AminoAcidCode.Alanine, 137).inserts(AminoAcidCode.Glycine, AminoAcidCode.Methionine).build();

        Assert.assertEquals("p.C136_A137insGM", format.format(pm));
    }

    @Test
    public void testFormatInsertionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 136, AminoAcidCode.Alanine, 137).inserts(AminoAcidCode.Glycine, AminoAcidCode.Methionine).build();

        Assert.assertEquals("p.Cys136_Ala137insGlyMet", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Ignore
    @Test
    public void testParseAAFsFix() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.E61fs", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Ignore
    @Test
    public void testParseAATerSubstitutionFix5() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Y553_K558>", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test
    public void parserShouldBeAbleToParseGaussVariantsFromBED() throws IOException {

        String filename = getClass().getResource("gauss_variants.tsv").getFile();

        Map<String, VariantTypeReport> report = collectVariantParsingReportFromBED(filename);

        Assert.assertEquals(0, VariantTypeReport.countParsingErrors(report));
    }

    @Test
    public void parserShouldBeAbleToParseStraussVariantsFromBED() throws IOException {

        String filename = getClass().getResource("strauss_variants.tsv").getFile();

        Map<String, VariantTypeReport> report = collectVariantParsingReportFromBED(filename);

        Assert.assertEquals(0, VariantTypeReport.countParsingErrors(report));
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

    public static class VariantTypeReport {

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