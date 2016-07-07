package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.DeletionAndInsertion;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

public class ProteinSequenceVariationHGVSParseDelinsTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    ///// DELETION FOLLOWED BY INSERTIONS

    @Test
    public void testParseDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399delinsL");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331delinsF");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420delinsSSDG");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Thr399delinsLeu");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Leu330_Ala331delinsPhe");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Asp419_Arg420delinsSerSerAspGly");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins1() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.T399>L", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("L"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins2() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.L330_A331>F", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("F"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.W39_E40>*", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Tryptophan, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getLastChangingAminoAcid());
        Assert.assertEquals(39, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(40, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("*"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseAANonStandardDelins4() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.D419_R420>SSDG", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("SSDG"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }
}