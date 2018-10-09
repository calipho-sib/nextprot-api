package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.text.ParseException;

public class SequenceVariantHGVSParseDuplicationTest {

    VariantHGVSFormat format = new VariantHGVSFormat();

    @Test
    public void testParseSimpleDuplication() throws ParseException, SequenceVariationBuildException {

        SequenceVariation duplication = format.parse("p.Val417dup");

        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.VALINE, duplication.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(417, duplication.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(417, duplication.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(417, duplication.getSequenceChange().getValue());
    }

    @Test
    public void testParseDuplication() throws Exception {

        SequenceVariation duplication = format.parse("p.Cys76_Glu79dup");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, duplication.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.GLUTAMIC_ACID, duplication.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(76, duplication.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(79, duplication.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.DUPLICATION, duplication.getSequenceChange().getType());
        Assert.assertEquals(79, duplication.getSequenceChange().getValue());
    }
}