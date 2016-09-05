package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.impl.Insertion;

import java.text.ParseException;

public class SequenceVariationHGVSParseInsertionTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();

    @Test
    public void testParseInsertion() throws Exception {

        SequenceVariation pm = format.parse("p.C136_A137insGM", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        assertProteinSequenceVariation(pm, AminoAcidCode.CYSTEINE, AminoAcidCode.ALANINE, 136);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("GM"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test(expected = ParseException.class)
    public void shouldContain2FlankingResidues() throws Exception {

        format.parse("p.C136_A138insGM", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore() throws ParseException {

        format.parse("p.Met1875-Glu1876insMet", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore2() throws ParseException {

        format.parse("p.Lys722-Ala723insTyrLys", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }

    @Test
    public void testParseInsertionsVariants() throws ParseException {

        SequenceVariation pm = format.parse("p.Met1875_Glu1876insMet", SequenceVariationHGVSFormat.ParsingMode.STRICT);

        assertProteinSequenceVariation(pm, AminoAcidCode.METHIONINE, AminoAcidCode.GLUTAMIC_ACID, 1875);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("M"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(1875, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testParseInsertionsVariants2() throws ParseException {

        SequenceVariation pm = format.parse("p.Lys722_Ala723insTyrLys", SequenceVariationHGVSFormat.ParsingMode.STRICT);

        assertProteinSequenceVariation(pm, AminoAcidCode.LYSINE, AminoAcidCode.ALANINE, 722);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("YK"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
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