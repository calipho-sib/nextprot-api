package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.Insertion;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;

import java.text.ParseException;

public class SequenceVariationHGVSParseInsertionTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testParseInsertion() throws Exception {

        SequenceVariation pm = format.parse("p.C136_A137insGM", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        assertProteinSequenceVariation(pm, AminoAcidCode.Cysteine, AminoAcidCode.Alanine, 136);
        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("GM"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test(expected = ParseException.class)
    public void shouldContain2FlankingResidues() throws Exception {

        format.parse("p.C136_A138insGM", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore() throws ParseException {

        format.parse("p.Met1875-Glu1876insMet", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore2() throws ParseException {

        format.parse("p.Lys722-Ala723insTyrLys", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test
    public void testParseInsertionsVariants() throws ParseException {

        SequenceVariation pm = format.parse("p.Met1875_Glu1876insMet", ProteinSequenceVariationHGVSFormat.ParsingMode.STRICT);

        assertProteinSequenceVariation(pm, AminoAcidCode.Methionine, AminoAcidCode.GlutamicAcid, 1875);
        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("M"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(1875, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testParseInsertionsVariants2() throws ParseException {

        SequenceVariation pm = format.parse("p.Lys722_Ala723insTyrLys", ProteinSequenceVariationHGVSFormat.ParsingMode.STRICT);

        assertProteinSequenceVariation(pm, AminoAcidCode.Lysine, AminoAcidCode.Alanine, 722);
        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfOneLetterCodeSequence("YK"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(722, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    public static void assertProteinSequenceVariation(SequenceVariation pm, AminoAcidCode expectedFirstAA,
                                                      AminoAcidCode expectedLastAA, int expectedFirstPos) {
        Assert.assertEquals(expectedFirstAA, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(expectedLastAA, pm.getLastChangingAminoAcid());
        Assert.assertEquals(expectedFirstPos, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(expectedFirstPos+1, pm.getLastChangingAminoAcidPos());
    }
}