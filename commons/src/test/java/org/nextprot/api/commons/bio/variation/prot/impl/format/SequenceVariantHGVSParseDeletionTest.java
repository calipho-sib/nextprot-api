package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

public class SequenceVariantHGVSParseDeletionTest {

    VariantHGVSFormat format = new VariantHGVSFormat();

    @Test
    public void testParseAADeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(73, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        SequenceVariation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(487, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(498, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAADeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(73, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(487, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(498, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion1() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.K487_L498del12");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(487, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(498, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.K487_L498delPRAL");

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(487, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(498, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
    }
}