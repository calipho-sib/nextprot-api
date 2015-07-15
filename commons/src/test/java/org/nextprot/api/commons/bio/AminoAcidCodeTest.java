package org.nextprot.api.commons.bio;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 09/07/15.
 */
public class AminoAcidCodeTest {

    @Test
    public void testValueOfCode1AAFound() throws Exception {

        Assert.assertEquals(AminoAcidCode.Alanine, AminoAcidCode.valueOfCode1AA('A'));
    }

    @Test
    public void testValueOfCodeFound() throws Exception {

        Assert.assertEquals(AminoAcidCode.Alanine, AminoAcidCode.valueOfCode("Ala"));
    }

    @Test
    public void testGetAACode1() throws Exception {

        Assert.assertEquals('A', AminoAcidCode.valueOfCode1AA('A').get1LetterCode());
    }

    @Test
    public void testGetAACode3() throws Exception {

        Assert.assertEquals("Ala", AminoAcidCode.valueOfCode1AA('A').get3LetterCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCode1AANotFound() throws Exception {

        AminoAcidCode.valueOfCode1AA('a');
    }

    @Test
     public void testGetStopCode1() throws Exception {

        Assert.assertEquals('*', AminoAcidCode.valueOfCode1AA('*').get1LetterCode());
    }

    @Test
    public void testGetStopCode3() throws Exception {

        Assert.assertEquals("Ter", AminoAcidCode.valueOfCode1AA('*').get3LetterCode());
    }
}