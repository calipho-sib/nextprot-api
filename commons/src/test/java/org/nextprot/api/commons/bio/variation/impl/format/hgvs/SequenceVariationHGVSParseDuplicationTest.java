package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

import java.text.ParseException;

public class SequenceVariationHGVSParseDuplicationTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();

    @Test
    public void testParseSimpleDuplication() throws ParseException {

        SequenceVariation duplication = format.parse("p.Val417dup");

        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getChangingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getChangingSequence().getLastAminoAcid());
        Assert.assertEquals(417, duplication.getChangingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(417, duplication.getChangingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(417, duplication.getSequenceChange().getValue());
    }

    @Test
    public void testParseDuplication() throws Exception {

        SequenceVariation duplication = format.parse("p.Cys76_Glu79dup");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, duplication.getChangingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.GLUTAMIC_ACID, duplication.getChangingSequence().getLastAminoAcid());
        Assert.assertEquals(76, duplication.getChangingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(79, duplication.getChangingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(79, duplication.getSequenceChange().getValue());
    }
}