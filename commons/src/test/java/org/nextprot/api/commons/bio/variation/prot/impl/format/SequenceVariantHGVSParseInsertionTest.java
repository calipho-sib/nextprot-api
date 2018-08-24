package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Insertion;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.text.ParseException;

public class SequenceVariantHGVSParseInsertionTest {

    VariantHGVSFormat format = new VariantHGVSFormat();

    @Test
    public void testParseInsertion() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.C136_A137insGM");

        assertProteinSequenceVariation(pm, AminoAcidCode.CYSTEINE, AminoAcidCode.ALANINE, 136);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("GM"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(136, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test(expected = ParseException.class)
    public void shouldContain2FlankingResidues() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.C136_A138insGM");
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore() throws ParseException, SequenceVariationBuildException {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.Met1875-Glu1876insMet");
    }

    @Test(expected = ParseException.class)
    public void testParseInsertionsVariantsInvalidColonInsteadOfUnderscore2() throws ParseException, SequenceVariationBuildException {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.Lys722-Ala723insTyrLys");
    }

    @Test
    public void testParseInsertionsVariants() throws ParseException, SequenceVariationBuildException {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.Met1875_Glu1876insMet");

        assertProteinSequenceVariation(pm, AminoAcidCode.METHIONINE, AminoAcidCode.GLUTAMIC_ACID, 1875);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("M"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(1875, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testParseInsertionsVariants2() throws ParseException, SequenceVariationBuildException {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.Lys722_Ala723insTyrLys");

        assertProteinSequenceVariation(pm, AminoAcidCode.LYSINE, AminoAcidCode.ALANINE, 722);
        Assert.assertEquals(SequenceChange.Type.INSERTION, pm.getSequenceChange().getType());
        Assert.assertArrayEquals(AminoAcidCode.valueOfAminoAcidCodeSequence("YK"), (AminoAcidCode[]) pm.getSequenceChange().getValue());
        Assert.assertEquals(722, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
    }

    public static void assertProteinSequenceVariation(SequenceVariation pm, AminoAcidCode expectedFirstAA,
                                                      AminoAcidCode expectedLastAA, int expectedFirstPos) {
        Assert.assertEquals(expectedFirstAA, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(expectedLastAA, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(expectedFirstPos, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(expectedFirstPos+1, pm.getVaryingSequence().getLastAminoAcidPos());
    }
}