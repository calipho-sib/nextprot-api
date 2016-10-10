package org.nextprot.api.commons.bio.variation.impl.format.bed;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.impl.AminoAcidModification;
import org.nextprot.api.commons.bio.variation.impl.SequenceVariationImpl;

import java.text.ParseException;

public class AminoAcidModificationBedFormatTest {

    private AminoAcidModificationBedFormat format = new AminoAcidModificationBedFormat();

    @Test
    public void testFormatWith3LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 54).thenAddModification(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals("SNO-Cys54", format.format(pm, AminoAcidCode.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatWith1LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 54).thenAddModification(AminoAcidModification.S_NITROSATION).build();

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

    // Not yet parsable !
    @Test(expected = ParseException.class)
    public void testParsingMultiplePtms() throws Exception {

        // P-Thr265 + P-Thr269 + P-Thr273
        SequenceVariation pm = format.parse("P-Thr265-Thr269-Thr273");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }
}