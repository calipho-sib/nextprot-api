package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.Deletion;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

public class ProteinSequenceVariationHGVSParseDeletionTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

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
}