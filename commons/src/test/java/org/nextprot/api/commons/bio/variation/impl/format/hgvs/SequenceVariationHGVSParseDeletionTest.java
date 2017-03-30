package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

public class SequenceVariationHGVSParseDeletionTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();

    @Test
    public void testParseAADeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAADeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion1() throws Exception {

        format = new SequenceVariantHGVSFormat(SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.K487_L498del12");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        format = new SequenceVariantHGVSFormat(SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.K487_L498delPRAL");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }
}