package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.AminoAcidModification;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Glycosylation;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.text.ParseException;

public class SequenceModificationBedFormatTest {

    private SequenceVariationFormat format = new SequenceModificationBedFormat();

    @Test
    public void testFormatWith3LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 54).thenAddModification(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals("SNO-Cys54", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatWith1LetterCodeAAMod() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 54).thenAddModification(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals("SNO-C54", format.format(pm, AminoAcidCode.CodeType.ONE_LETTER));
    }

    @Test
    public void testParsing3LetterCodeAAFormat() throws Exception {

        SequenceVariation pm = format.parse("SNO-Cys54");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }

    @Test
    public void testParsing1LetterCodeAAFormat() throws Exception {

        SequenceVariation pm = format.parse("SNO-C54");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }

    // Not yet parsable !
    @Test(expected = ParseException.class)
    public void testParsingMultiplePtms() throws Exception {

        // P-Thr265 + P-Thr269 + P-Thr273
        SequenceVariation pm = format.parse("P-Thr265-Thr269-Thr273");

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange());
    }

    @Test
    public void testFormatPTMWith1LetterCodeAA() {

        format = new SequenceGlycosylationBedFormat();

        SequenceVariation glycosylation = new SequenceVariationImpl.StartBuilding()
                .selectAminoAcid(AminoAcidCode.ASPARAGINE, 21)
                .thenAddModification(new Glycosylation("PTM-0528"))
                .build();

        Assert.assertEquals("PTM-0528_Asn21", format.format(glycosylation, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testParsingSingleGlyco() throws ParseException {

        SequenceGlycosylationBedFormat format = new SequenceGlycosylationBedFormat();

        SequenceVariation pm = format.parse("PTM-0528_21", "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
        Assert.assertEquals(AminoAcidCode.ASPARAGINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(21, pm.getVaryingSequence().getFirstAminoAcidPos());

        SequenceChange<?> change = pm.getSequenceChange();
        Assert.assertEquals(AminoAcidModification.GLYCOSYLATION, change.getValue());
        Assert.assertTrue(change instanceof Glycosylation);

        Glycosylation glyco = (Glycosylation) change;
        Assert.assertEquals("PTM-0528", glyco.getPTMId());

    }
}