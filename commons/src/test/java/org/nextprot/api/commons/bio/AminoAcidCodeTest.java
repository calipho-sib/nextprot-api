package org.nextprot.api.commons.bio;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 09/07/15.
 */
public class AminoAcidCodeTest {

    @Test
    public void testValueOfCode1Found() throws Exception {

        Assert.assertEquals(AminoAcidCode.Alanine, AminoAcidCode.valueOfOneLetterCode('A'));
    }

    @Test
    public void testValueOfCode1AlsoFound() throws Exception {

        Assert.assertEquals(AminoAcidCode.Stop, AminoAcidCode.valueOfAminoAcid("*"));
    }

    @Test
    public void testValueOfCode3Found() throws Exception {

        Assert.assertEquals(AminoAcidCode.Alanine, AminoAcidCode.valueOfAminoAcid("Ala"));
    }

    @Test
    public void testValueOfCodeSequenceOk() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Tryptophan, AminoAcidCode.Tryptophan, AminoAcidCode.Alanine, AminoAcidCode.Tyrosine}, AminoAcidCode.valueOfOneLetterCodeSequence("TrpWAlaY"));
    }

    @Test
    public void testValueOfCodeSequence2Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Stop}, AminoAcidCode.valueOfOneLetterCodeSequence("Ter"));
    }

    @Test
    public void testValueOfCodeSequence3Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Threonine, AminoAcidCode.Tyrosine, AminoAcidCode.Stop}, AminoAcidCode.valueOfOneLetterCodeSequence("TY*"));
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
}