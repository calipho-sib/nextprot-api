package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcid;
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

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Arginine, 54).substitutedBy(AminoAcid.Cysteine).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionWithStop() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.GlutamicAcid, 31).substitutedBy(AminoAcid.Stop).build();

        Assert.assertEquals("p.E31*", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Lysine, 73).deleted().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 487, AminoAcid.Leucine, 498).deleted().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.M682fs*1", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Threonine, 399).deletedAndInserts(AminoAcid.Leucine).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Leucine, 330, AminoAcid.Alanine, 331).deletedAndInserts(AminoAcid.Phenylalanine).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.AsparticAcid, 419, AminoAcid.Arginine, 420).deletedAndInserts(AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Arginine, 54).substitutedBy(AminoAcid.Cysteine).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Lysine, 73).deleted().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 487, AminoAcid.Leucine, 498).deleted().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.Met682fsTer1", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Threonine, 399).deletedAndInserts(AminoAcid.Leucine).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Leucine, 330, AminoAcid.Alanine, 331).deletedAndInserts(AminoAcid.Phenylalanine).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.AsparticAcid, 419, AminoAcid.Arginine, 420).deletedAndInserts(AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testParseSubstitution() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcid.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode1AA() throws Exception {

        format.parse("p.B54C");
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode3AA() throws Exception {

        format.parse("p.Mat54Trp");
    }

    @Test
    public void testParseSubstitutionStop() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54*");

        Assert.assertEquals(AminoAcid.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.Stop, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAADeletion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.M682fs*1");

        Assert.assertEquals(AminoAcid.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399delinsL");

        Assert.assertEquals(AminoAcid.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("L"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331delinsF");

        Assert.assertEquals(AminoAcid.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("F"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420delinsSSDG");

        Assert.assertEquals(AminoAcid.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("SSDG"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcid.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Met682fsTer1");

        Assert.assertEquals(AminoAcid.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Thr399delinsLeu");

        Assert.assertEquals(AminoAcid.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("L"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Leu330_Ala331delinsPhe");

        Assert.assertEquals(AminoAcid.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("F"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Asp419_Arg420delinsSerSerAspGly");

        Assert.assertEquals(AminoAcid.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("SSDG"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.R54C", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDeletion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498del12", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcid.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.K487_L498delPRAL", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcid.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.S1476fs*>9", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Serine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1476, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(9, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399>L", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("L"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins2() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331>F", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("F"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.W39_E40>*", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Tryptophan, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.GlutamicAcid, pm.getLastChangingAminoAcid());
        Assert.assertEquals(39, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(40, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("*"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins4() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420>SSDG", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("SSDG"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.*104E", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.GlutamicAcid, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Ter104Glu", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.GlutamicAcid, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testFormatSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Stop, 104).substitutedBy(AminoAcid.GlutamicAcid).build();

        Assert.assertEquals("p.*104E", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Stop, 104).substitutedBy(AminoAcid.GlutamicAcid).build();

        Assert.assertEquals("p.Ter104Glu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testParseInsertion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.C136_A137insGM", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcid.Cysteine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcid.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(136, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(137, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcid.valueOfOneLetterCodeSequence("GM"), (AminoAcid[]) pm.getProteinSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testFormatInsertionCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Cysteine, 136, AminoAcid.Alanine, 137).inserts(AminoAcid.Glycine, AminoAcid.Methionine).build();

        Assert.assertEquals("p.C136_A137insGM", format.format(pm));
    }

    @Test
    public void testFormatInsertionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Cysteine, 136, AminoAcid.Alanine, 137).inserts(AminoAcid.Glycine, AminoAcid.Methionine).build();

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

    @Test
    public void parserShouldBeAbleToParseVariantsFromBED() throws IOException {

        parseVariants(getClass().getResource("variants.tsv").getFile());
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