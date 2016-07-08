package org.nextprot.api.commons.bio.variation.seq.impl.format.bed;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.impl.AminoAcidModification;
import org.nextprot.api.commons.bio.variation.seq.impl.SequenceVariationImpl;

public class AminoAcidModificationBedFormatTest {

    private AminoAcidModificationBedFormat format = new AminoAcidModificationBedFormat();

    @Test
    public void testFormatWith3LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilder().aminoAcid(AminoAcidCode.CYSTEINE, 54).modifies(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals("SNO-Cys54", format.format(pm, AminoAcidCode.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatWith1LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilder().aminoAcid(AminoAcidCode.CYSTEINE, 54).modifies(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals("SNO-C54", format.format(pm, AminoAcidCode.AACodeType.ONE_LETTER));
    }

    @Test
    public void testParsing3LetterCodeAAFormat() throws Exception {

        SequenceVariation pm = format.parse("SNO-Cys54");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }

    @Test
    public void testParsing1LetterCodeAAFormat() throws Exception {

        SequenceVariation pm = format.parse("SNO-C54");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }
}