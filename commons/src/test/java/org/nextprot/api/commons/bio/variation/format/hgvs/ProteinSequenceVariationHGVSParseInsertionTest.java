package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.Insertion;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseInsertionTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testParseInsertion() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.C136_A137insGM", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        assertProteinSequenceVariation(pm, AminoAcidCode.Cysteine, AminoAcidCode.Alanine, 136, 137);
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("GM"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore() throws ParseException {

        format.parse("p.Met1875-Glu1876insMet", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test
    public void testParseInsertionsVariants() throws ParseException {

        ProteinSequenceVariation pm = format.parse("p.Met1875_Glu1876insMet", ProteinSequenceVariationHGVSFormat.ParsingMode.STRICT);

        assertProteinSequenceVariation(pm, AminoAcidCode.Methionine, AminoAcidCode.GlutamicAcid, 1875, 1876);
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("M"), (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
        Assert.assertEquals(1875, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
    }

    public static void assertProteinSequenceVariation(ProteinSequenceVariation pm, AminoAcidCode expectedFirstAA,
                                                      AminoAcidCode expectedLastAA, int expectedFirstPos, int expectedLastPos) {
        Assert.assertEquals(expectedFirstAA, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(expectedLastAA, pm.getLastChangingAminoAcid());
        Assert.assertEquals(expectedFirstPos, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(expectedLastPos, pm.getLastChangingAminoAcidPos());
    }
}