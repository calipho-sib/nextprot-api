package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

public class ProteinSequenceVariationHGVSFormatTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testFormatSubstitution() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionWithStop() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.GlutamicAcid, 31).substitutedBy(AminoAcidCode.Stop).build();

        Assert.assertEquals("p.E31*", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deletes().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deletes().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(AminoAcidCode.Alanine, 2).build();

        Assert.assertEquals("p.M682Afs*2", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deletes().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deletes().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(AminoAcidCode.Alanine, 2).build();

        Assert.assertEquals("p.Met682AlafsTer2", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatSubstitutionFixCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Stop, 104).substitutedBy(AminoAcidCode.GlutamicAcid).build();

        Assert.assertEquals("p.*104E", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionFixCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Stop, 104).substitutedBy(AminoAcidCode.GlutamicAcid).build();

        Assert.assertEquals("p.Ter104Glu", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatInsertionCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 136, AminoAcidCode.Alanine, 137).inserts(AminoAcidCode.Glycine, AminoAcidCode.Methionine).build();

        Assert.assertEquals("p.C136_A137insGM", format.format(pm));
    }

    @Test
    public void testFormatInsertionCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 136, AminoAcidCode.Alanine, 137).inserts(AminoAcidCode.Glycine, AminoAcidCode.Methionine).build();

        Assert.assertEquals("p.Cys136_Ala137insGlyMet", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDuplicationSimpleCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Cysteine, 76).duplicates().build();

        Assert.assertEquals("p.C76dup", format.format(pm, ProteinSequenceVariationFormat.AACodeType.ONE_LETTER));
    }

    @Test
    public void testFormatDuplicationSimpleCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Cysteine, 76).duplicates().build();

        Assert.assertEquals("p.Cys76dup", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDuplicationCode1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 76, AminoAcidCode.GlutamicAcid, 79).duplicates().build();

        Assert.assertEquals("p.C76_E79dup", format.format(pm, ProteinSequenceVariationFormat.AACodeType.ONE_LETTER));
    }

    @Test
    public void testFormatDuplicationCode3() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Cysteine, 76, AminoAcidCode.GlutamicAcid, 79).duplicates().build();

        Assert.assertEquals("p.Cys76_Glu79dup", format.format(pm, ProteinSequenceVariationFormat.AACodeType.THREE_LETTER));
    }
}