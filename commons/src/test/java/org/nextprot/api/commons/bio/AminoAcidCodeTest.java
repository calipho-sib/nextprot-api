package org.nextprot.api.commons.bio;

import org.junit.Assert;
import org.junit.Test;

public class AminoAcidCodeTest {

    @Test
    public void testValueOfCode1Found() throws Exception {

        Assert.assertEquals(AminoAcidCode.ALANINE, AminoAcidCode.valueOfAminoAcid("A"));
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

        Assert.assertEquals("A", AminoAcidCode.ALANINE.get1LetterCode());
    }

    @Test
    public void testGetAACode3() throws Exception {

        Assert.assertEquals("Ala", AminoAcidCode.ALANINE.get3LetterCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCode1AANotFound() throws Exception {

        AminoAcidCode.valueOfAminoAcid("a");
    }

    @Test
     public void testGetStopCode1() throws Exception {

        Assert.assertEquals("*", AminoAcidCode.STOP.get1LetterCode());
    }

    @Test
    public void testGetStopCode3() throws Exception {

        Assert.assertEquals("Ter", AminoAcidCode.STOP.get3LetterCode());
    }

    @Test
    public void testAsArray() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[] {AminoAcidCode.ASPARTIC_ACID}, AminoAcidCode.asArray(AminoAcidCode.ASPARTIC_ACID));
    }

    @Test
    public void testGetXaaCode3() throws Exception {

        Assert.assertEquals(AminoAcidAmbiguityCode.XAA, AminoAcidAmbiguityCode.valueOf("XAA"));
        Assert.assertEquals("X", AminoAcidAmbiguityCode.XAA.get1LetterCode());
        Assert.assertEquals("Xaa", AminoAcidAmbiguityCode.XAA.get3LetterCode());

        for (AminoAcidCode aac : AminoAcidCode.values())
            Assert.assertTrue(AminoAcidAmbiguityCode.XAA.match(aac));
    }

    @Test
    public void testGetAsxCode3() throws Exception {

        Assert.assertEquals(AminoAcidAmbiguityCode.ASX, AminoAcidAmbiguityCode.valueOf("ASX"));
        Assert.assertEquals("B", AminoAcidAmbiguityCode.ASX.get1LetterCode());
        Assert.assertEquals("Asx", AminoAcidAmbiguityCode.ASX.get3LetterCode());

        Assert.assertTrue(AminoAcidAmbiguityCode.ASX.match(AminoAcidCode.ASPARTIC_ACID));
        Assert.assertTrue(AminoAcidAmbiguityCode.ASX.match(AminoAcidCode.ASPARAGINE));
    }

    @Test
    public void testGetGlxCode3() throws Exception {

        Assert.assertEquals(AminoAcidAmbiguityCode.GLX, AminoAcidAmbiguityCode.valueOf("GLX"));
        Assert.assertEquals("Z", AminoAcidAmbiguityCode.GLX.get1LetterCode());
        Assert.assertEquals("Glx", AminoAcidAmbiguityCode.GLX.get3LetterCode());

        Assert.assertTrue(AminoAcidAmbiguityCode.GLX.match(AminoAcidCode.GLUTAMIC_ACID));
        Assert.assertTrue(AminoAcidAmbiguityCode.GLX.match(AminoAcidCode.GLUTAMINE));
    }

    @Test
    public void testGetXleCode3() throws Exception {

        Assert.assertEquals(AminoAcidAmbiguityCode.XLE, AminoAcidAmbiguityCode.valueOf("XLE"));
        Assert.assertEquals("J", AminoAcidAmbiguityCode.XLE.get1LetterCode());
        Assert.assertEquals("Xle", AminoAcidAmbiguityCode.XLE.get3LetterCode());

        Assert.assertTrue(AminoAcidAmbiguityCode.XLE.match(AminoAcidCode.ISOLEUCINE));
        Assert.assertTrue(AminoAcidAmbiguityCode.XLE.match(AminoAcidCode.LEUCINE));
    }
}