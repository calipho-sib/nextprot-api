package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcid;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

public class ProteinSequenceVariationHGVSFormatTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testFormatSubstitution() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Arginine, 54).substitutedBy(AminoAcid.Cysteine).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionWithStop() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.GlutamicAcid, 31).substitutedBy(AminoAcid.Stop).build();

        Assert.assertEquals("p.E31*", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Lysine, 73).deleted().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 487, AminoAcid.Leucine, 498).deleted().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.M682fs*1", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Threonine, 399).deletedAndInserts(AminoAcid.Leucine).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Leucine, 330, AminoAcid.Alanine, 331).deletedAndInserts(AminoAcid.Phenylalanine).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.AsparticAcid, 419, AminoAcid.Arginine, 420).deletedAndInserts(AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Arginine, 54).substitutedBy(AminoAcid.Cysteine).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Lysine, 73).deleted().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 487, AminoAcid.Leucine, 498).deleted().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.Met682fsTer1", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Threonine, 399).deletedAndInserts(AminoAcid.Leucine).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Leucine, 330, AminoAcid.Alanine, 331).deletedAndInserts(AminoAcid.Phenylalanine).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.AsparticAcid, 419, AminoAcid.Arginine, 420).deletedAndInserts(AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Stop, 104).substitutedBy(AminoAcid.GlutamicAcid).build();

        Assert.assertEquals("p.*104E", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Stop, 104).substitutedBy(AminoAcid.GlutamicAcid).build();

        Assert.assertEquals("p.Ter104Glu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatInsertionCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Cysteine, 136, AminoAcid.Alanine, 137).inserts(AminoAcid.Glycine, AminoAcid.Methionine).build();

        Assert.assertEquals("p.C136_A137insGM", format.format(pm));
    }

    @Test
    public void testFormatInsertionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Cysteine, 136, AminoAcid.Alanine, 137).inserts(AminoAcid.Glycine, AminoAcid.Methionine).build();

        Assert.assertEquals("p.Cys136_Ala137insGlyMet", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }
}