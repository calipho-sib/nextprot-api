package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcid;
import org.nextprot.api.commons.bio.variation.Insertion;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseInsertionTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

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

    @Ignore
    @Test
    public void testParseInsertionsVariants() throws ParseException {
        /*
INSERTIONS:
p.Met1875-Glu1876insMet
         */
        format.parse("p.Met1875-Glu1876insMet", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }
}