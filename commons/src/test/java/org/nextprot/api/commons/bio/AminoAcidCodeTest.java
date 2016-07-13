package org.nextprot.api.commons.bio;

import org.junit.Assert;
import org.junit.Test;

public class AminoAcidCodeTest {

    @Test
    public void testValueOfCode1Found() throws Exception {

        Assert.assertEquals(AminoAcidCode.ALANINE, AminoAcidCode.valueOfOneLetterCode('A'));
    }

    @Test
    public void testValueOfCode1AlsoFound() throws Exception {

        Assert.assertEquals(AminoAcidCode.STOP, AminoAcidCode.valueOfAminoAcid("*"));
    }

    @Test
    public void testValueOfCode3Found() throws Exception {

        Assert.assertEquals(AminoAcidCode.ALANINE, AminoAcidCode.valueOfAminoAcid("Ala"));
    }

    @Test
    public void testValueOfCodeSequenceOk() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.TRYPTOPHAN, AminoAcidCode.TRYPTOPHAN, AminoAcidCode.ALANINE, AminoAcidCode.TYROSINE}, AminoAcidCode.valueOfOneLetterCodeSequence("TrpWAlaY"));
    }

    @Test
    public void testValueOfCodeSequence2Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.STOP}, AminoAcidCode.valueOfOneLetterCodeSequence("Ter"));
    }

    @Test
    public void testValueOfCodeSequence3Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.THREONINE, AminoAcidCode.TYROSINE, AminoAcidCode.STOP}, AminoAcidCode.valueOfOneLetterCodeSequence("TY*"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCodeSequenceFirstInvalid() throws Exception {

        AminoAcidCode.valueOfOneLetterCodeSequence("trpWAlaY");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCodeSequenceOtherInvalid() throws Exception {

        AminoAcidCode.valueOfOneLetterCodeSequence("TrpWalaY");
    }

    @Test
    public void testGetAACode1() throws Exception {

        Assert.assertEquals('A', AminoAcidCode.valueOfOneLetterCode('A').get1LetterCode());
    }

    @Test
    public void testGetAACode3() throws Exception {

        Assert.assertEquals("Ala", AminoAcidCode.valueOfOneLetterCode('A').get3LetterCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCode1AANotFound() throws Exception {

        AminoAcidCode.valueOfOneLetterCode('a');
    }

    @Test
     public void testGetStopCode1() throws Exception {

        Assert.assertEquals('*', AminoAcidCode.valueOfOneLetterCode('*').get1LetterCode());
    }

    @Test
    public void testGetStopCode3() throws Exception {

        Assert.assertEquals("Ter", AminoAcidCode.valueOfOneLetterCode('*').get3LetterCode());
    }

    @Test
    public void testAsArray() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[] {AminoAcidCode.ASPARTIC_ACID}, AminoAcidCode.asArray(AminoAcidCode.ASPARTIC_ACID));
    }
}