package org.nextprot.api.commons.bio;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 09/07/15.
 */
public class AminoAcidTest {

    @Test
    public void testValueOfCode1Found() throws Exception {

        Assert.assertEquals(AminoAcid.Alanine, AminoAcid.valueOfOneLetterCode('A'));
    }

    @Test
    public void testValueOfCode1AlsoFound() throws Exception {

        Assert.assertEquals(AminoAcid.Stop, AminoAcid.valueOfAminoAcid("*"));
    }

    @Test
    public void testValueOfCode3Found() throws Exception {

        Assert.assertEquals(AminoAcid.Alanine, AminoAcid.valueOfAminoAcid("Ala"));
    }

    @Test
    public void testValueOfCodeSequenceOk() throws Exception {

        Assert.assertArrayEquals(new AminoAcid[]{AminoAcid.Tryptophan, AminoAcid.Tryptophan, AminoAcid.Alanine, AminoAcid.Tyrosine}, AminoAcid.valueOfOneLetterCodeSequence("TrpWAlaY"));
    }

    @Test
    public void testValueOfCodeSequence2Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcid[]{AminoAcid.Stop}, AminoAcid.valueOfOneLetterCodeSequence("Ter"));
    }

    @Test
    public void testValueOfCodeSequence3Ok() throws Exception {

        Assert.assertArrayEquals(new AminoAcid[]{AminoAcid.Threonine, AminoAcid.Tyrosine, AminoAcid.Stop}, AminoAcid.valueOfOneLetterCodeSequence("TY*"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCodeSequenceFirstInvalid() throws Exception {

        AminoAcid.valueOfOneLetterCodeSequence("trpWAlaY");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCodeSequenceOtherInvalid() throws Exception {

        AminoAcid.valueOfOneLetterCodeSequence("TrpWalaY");
    }

    @Test
    public void testGetAACode1() throws Exception {

        Assert.assertEquals('A', AminoAcid.valueOfOneLetterCode('A').get1LetterCode());
    }

    @Test
    public void testGetAACode3() throws Exception {

        Assert.assertEquals("Ala", AminoAcid.valueOfOneLetterCode('A').get3LetterCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCode1AANotFound() throws Exception {

        AminoAcid.valueOfOneLetterCode('a');
    }

    @Test
     public void testGetStopCode1() throws Exception {

        Assert.assertEquals('*', AminoAcid.valueOfOneLetterCode('*').get1LetterCode());
    }

    @Test
    public void testGetStopCode3() throws Exception {

        Assert.assertEquals("Ter", AminoAcid.valueOfOneLetterCode('*').get3LetterCode());
    }
}