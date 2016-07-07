package org.nextprot.api.commons.bio.variation.seq.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceChange;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;

public class SequenceVariationHGVSParseDeletionTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();

    @Test
    public void testParseAADeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAADeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion1() throws Exception {

        SequenceVariation pm = format.parse("p.K487_L498del12", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        SequenceVariation pm = format.parse("p.K487_L498delPRAL", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }
}