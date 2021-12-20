package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Substitution;

import java.text.ParseException;

import static org.junit.Assert.fail;

public class SequenceVariantHGVSParseSubstitutionTest {

    VariantHGVSFormat format = new VariantHGVSFormat();


    @Test(expected = ParseException.class)
    public void testParseUnknownCode1AA() throws Exception {

        format.parse("p._54C");
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode3AA() throws Exception {

        format.parse("p.Mat54Trp");
    }

    ///// SUBSTITUTIONS
    @Test
    public void testParseSubstitution() throws Exception {

        SequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(54, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionStop() throws Exception {

        SequenceVariation pm = format.parse("p.R54*");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(54, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.STOP, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(54, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(54, pm.getVaryingSequence().getLastAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test(expected = ParseException.class)
    public void testParseAATerSubstitutionFixCode1() throws Exception {
        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        try {
            format.parse("p.*104E");
            fail();
        } catch (ParseException e) {
            if (e.getMessage().contains("should not contain STOP codon as affected amino acid")) {
                throw e; // success tests
            } else {
                fail();
            }
        }
    }
    
    @Test(expected = ParseException.class)
    public void testParseAATerSubstitutionFixCode3() throws Exception {
        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        try {
            format.parse("p.Ter104Glu");
            fail();
        } catch (ParseException e) {
            if (e.getMessage().contains("should not contain STOP codon as affected amino acid")) {
                throw e; // success tests
            } else {
                fail();
            }
        }
    }

    @Ignore
    @Test
    public void testParseAATerSubstitutionFix5() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.Y553_K558>");
    }
}