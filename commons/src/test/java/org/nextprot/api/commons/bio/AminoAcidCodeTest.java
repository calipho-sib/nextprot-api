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

        Assert.assertEquals(AminoAcidCode.XAA, AminoAcidCode.valueOf("XAA"));
        Assert.assertEquals("X", AminoAcidCode.XAA.get1LetterCode());
        Assert.assertEquals("Xaa", AminoAcidCode.XAA.get3LetterCode());

        for (AminoAcidCode aac : AminoAcidCode.nonAmbiguousAminoAcidValues())
            Assert.assertTrue(AminoAcidCode.XAA.match(aac));
    }

    @Test
    public void testGetAsxCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.ASX, AminoAcidCode.valueOf("ASX"));
        Assert.assertEquals("B", AminoAcidCode.ASX.get1LetterCode());
        Assert.assertEquals("Asx", AminoAcidCode.ASX.get3LetterCode());

        Assert.assertTrue(AminoAcidCode.ASX.match(AminoAcidCode.ASPARTIC_ACID));
        Assert.assertTrue(AminoAcidCode.ASX.match(AminoAcidCode.ASPARAGINE));
    }

    @Test
    public void testGetGlxCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.GLX, AminoAcidCode.valueOf("GLX"));
        Assert.assertEquals("Z", AminoAcidCode.GLX.get1LetterCode());
        Assert.assertEquals("Glx", AminoAcidCode.GLX.get3LetterCode());

        Assert.assertTrue(AminoAcidCode.GLX.match(AminoAcidCode.GLUTAMIC_ACID));
        Assert.assertTrue(AminoAcidCode.GLX.match(AminoAcidCode.GLUTAMINE));
    }

    @Test
    public void testGetXleCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.XLE, AminoAcidCode.valueOf("XLE"));
        Assert.assertEquals("J", AminoAcidCode.XLE.get1LetterCode());
        Assert.assertEquals("Xle", AminoAcidCode.XLE.get3LetterCode());

        Assert.assertTrue(AminoAcidCode.XLE.match(AminoAcidCode.ISOLEUCINE));
        Assert.assertTrue(AminoAcidCode.XLE.match(AminoAcidCode.LEUCINE));
    }

    @Test
    public void testValueOfCodeSequenceWithAmbiguity() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.ALANINE, AminoAcidCode.XAA, AminoAcidCode.TYROSINE}, AminoAcidCode.valueOfOneLetterCodeSequence("AXY"));
    }
}