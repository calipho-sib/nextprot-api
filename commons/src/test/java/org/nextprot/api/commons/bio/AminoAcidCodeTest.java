package org.nextprot.api.commons.bio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void testValueOfCodeSequenceExplicit3LetterCode() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.TRYPTOPHAN, AminoAcidCode.TRYPTOPHAN, AminoAcidCode.ALANINE, AminoAcidCode.TYROSINE},
                AminoAcidCode.valueOfAminoAcidCodeSequence("TrpTrpAlaTyr", AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testValueOfCodeSequenceImplicit3LetterCode() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.TRYPTOPHAN, AminoAcidCode.TRYPTOPHAN, AminoAcidCode.ALANINE, AminoAcidCode.TYROSINE},
                AminoAcidCode.valueOfAminoAcidCodeSequence("TrpTrpAlaTyr"));
    }

    @Test
    public void testValueOfCodeSequenceExplicit3LetterCodeStop() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.STOP},
                AminoAcidCode.valueOfAminoAcidCodeSequence("Ter", AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testValueOfCodeSequenceImplicit3LetterCodeStop() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.STOP},
                AminoAcidCode.valueOfAminoAcidCodeSequence("Ter"));
    }

    @Test
    public void testValueOfCodeSequenceExplicit1LetterCode() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.THREONINE, AminoAcidCode.TYROSINE, AminoAcidCode.STOP},
                AminoAcidCode.valueOfAminoAcidCodeSequence("TY*", AminoAcidCode.CodeType.ONE_LETTER));
    }

    @Test
    public void testValueOfCodeSequenceImplicit1LetterCode() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.THREONINE, AminoAcidCode.TYROSINE, AminoAcidCode.STOP},
                AminoAcidCode.valueOfAminoAcidCodeSequence("TY*"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfCodeSequenceFirstInvalid() throws Exception {

        AminoAcidCode.valueOfAminoAcidCodeSequence("TrpWAlaY", AminoAcidCode.CodeType.THREE_LETTER);
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

        Assert.assertArrayEquals(new AminoAcidCode[] {AminoAcidCode.ASPARTIC_ACID}, new AminoAcidCode[] { AminoAcidCode.ASPARTIC_ACID} );
    }

    @Test
    public void testGetXaaCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.XAA, AminoAcidCode.valueOf("XAA"));
        Assert.assertEquals("X", AminoAcidCode.XAA.get1LetterCode());
        Assert.assertEquals("Xaa", AminoAcidCode.XAA.get3LetterCode());
    }

    @Test
    public void testGetAsxCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.ASX, AminoAcidCode.valueOf("ASX"));
        Assert.assertEquals("B", AminoAcidCode.ASX.get1LetterCode());
        Assert.assertEquals("Asx", AminoAcidCode.ASX.get3LetterCode());
    }

    @Test
    public void testGetGlxCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.GLX, AminoAcidCode.valueOf("GLX"));
        Assert.assertEquals("Z", AminoAcidCode.GLX.get1LetterCode());
        Assert.assertEquals("Glx", AminoAcidCode.GLX.get3LetterCode());
    }

    @Test
    public void testGetXleCode3() throws Exception {

        Assert.assertEquals(AminoAcidCode.XLE, AminoAcidCode.valueOf("XLE"));
        Assert.assertEquals("J", AminoAcidCode.XLE.get1LetterCode());
        Assert.assertEquals("Xle", AminoAcidCode.XLE.get3LetterCode());
    }

    @Test
    public void testValueOfCodeSequenceWithAmbiguity() throws Exception {

        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.ALANINE, AminoAcidCode.XAA, AminoAcidCode.TYROSINE},
                AminoAcidCode.valueOfAminoAcidCodeSequence("AXY"));
    }

    @Test
    public void testCountAllAminoAcidCodesByCategory() {

        Assert.assertEquals(4, AminoAcidCode.ambiguousAminoAcidValues().size());
        Assert.assertEquals(23, AminoAcidCode.nonAmbiguousAminoAcidValues().size());
        Assert.assertEquals(AminoAcidCode.ambiguousAminoAcidValues().size()+AminoAcidCode.nonAmbiguousAminoAcidValues().size(),
                AminoAcidCode.values().length);
    }

    @Test
    public void ambiguousAminoAcidsAreAmbiguous() throws Exception {

        for (AminoAcidCode aac : AminoAcidCode.ambiguousAminoAcidValues()) {
            Assert.assertTrue(aac.isAmbiguous());
        }
    }

    @Test
    public void nonAmbiguousAminoAcidShouldMatchASingleAminoAcid() throws Exception {

        for (AminoAcidCode aac : AminoAcidCode.nonAmbiguousAminoAcidValues()) {
            Assert.assertTrue(!aac.isAmbiguous());
            Assert.assertTrue(aac.match(aac));
        }

        Assert.assertTrue(!AminoAcidCode.LEUCINE.match(AminoAcidCode.ISOLEUCINE));
    }

    @Test
    public void XaaShouldMatchAllAminoAcids() throws Exception {

        for (AminoAcidCode aac : AminoAcidCode.nonAmbiguousAminoAcidValues()) {
            Assert.assertTrue(AminoAcidCode.XAA.match(aac));
        }
    }

    @Test
    public void AsxShouldMatchAspOrAsn() throws Exception {

        Assert.assertTrue(AminoAcidCode.ASX.match(AminoAcidCode.ASPARTIC_ACID));
        Assert.assertTrue(AminoAcidCode.ASX.match(AminoAcidCode.ASPARAGINE));
    }

    @Test
    public void GlxShouldMatchGluOrGln() throws Exception {

        Assert.assertTrue(AminoAcidCode.GLX.match(AminoAcidCode.GLUTAMIC_ACID));
        Assert.assertTrue(AminoAcidCode.GLX.match(AminoAcidCode.GLUTAMINE));
    }

    @Test
    public void XleShouldMatchIleOrLeu() {

        Assert.assertTrue(AminoAcidCode.XLE.match(AminoAcidCode.ISOLEUCINE));
        Assert.assertTrue(AminoAcidCode.XLE.match(AminoAcidCode.LEUCINE));
    }
    
    @Test
    public void All_64_Codons_Should_Translate_To_21_AminoAcids() {
    	List<String> nulist = new ArrayList<>() ;
    	nulist.add("A"); nulist.add("C"); nulist.add("G"); nulist.add("T");
    	int codonCnt = 0;
    	Set<String> aas = new HashSet<>();
    	for (String nu1:  nulist) {
        	for (String nu2:  nulist) {
            	for (String nu3:  nulist) {
            		codonCnt++;
            		String codon = nu1 + nu2 + nu3;
                	AminoAcidCode aa = AminoAcidCode.valueOfAminoAcidFromCodon(codon);
                	aas.add(aa.get3LetterCode());
                	//System.out.println(""+cnt + ":" + codon + " = " + aa);
            	}
        	}
    	}
    	Assert.assertEquals(64, codonCnt);
    	Assert.assertEquals(21, aas.size());
    	
    }

}