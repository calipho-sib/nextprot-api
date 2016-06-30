package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.Duplication;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseDuplicationTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testParseSimpleDuplication() throws ParseException {

        ProteinSequenceVariation duplication = format.parse("p.Val417dup");

        Assert.assertEquals(AminoAcidCode.Valine, duplication.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Valine, duplication.getLastChangingAminoAcid());
        Assert.assertEquals(417, duplication.getFirstChangingAminoAcidPos());
        Assert.assertEquals(417, duplication.getLastChangingAminoAcidPos());
        Assert.assertTrue(duplication.getProteinSequenceChange() instanceof Duplication);
        Assert.assertEquals(417, duplication.getProteinSequenceChange().getValue());
    }

    @Test
    public void testParseDuplication() throws Exception {

        ProteinSequenceVariation duplication = format.parse("p.Cys76_Glu79dup");

        Assert.assertEquals(AminoAcidCode.Cysteine, duplication.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, duplication.getLastChangingAminoAcid());
        Assert.assertEquals(76, duplication.getFirstChangingAminoAcidPos());
        Assert.assertEquals(79, duplication.getLastChangingAminoAcidPos());
        Assert.assertTrue(duplication.getProteinSequenceChange() instanceof Duplication);
        Assert.assertEquals(79, duplication.getProteinSequenceChange().getValue());
    }
}