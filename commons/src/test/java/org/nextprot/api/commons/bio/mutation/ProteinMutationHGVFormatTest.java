package org.nextprot.api.commons.bio.mutation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;

public class ProteinMutationHGVFormatTest {

    ProteinMutationHGVFormat format = new ProteinMutationHGVFormat();

    @Test
    public void testFormatSubstitution() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.M682fs*1", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals("p.Met682fsTer1", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, ProteinMutationFormat.AACodeType.THREE_LETTER));
    }

    @Test
    public void testParseSubstitution() throws Exception {

        ProteinMutation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(54, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(54, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getMutation().getValue());
    }

    @Test
    public void testParseAADeletion() throws Exception {

        ProteinMutation pm = format.parse("p.K73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(73, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletion() throws Exception {

        ProteinMutation pm = format.parse("p.K487_L498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(487, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(498, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseFrameshift() throws Exception {

        ProteinMutation pm = format.parse("p.M682fs*1");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(682, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Frameshift);
        Assert.assertEquals(1, pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1() throws Exception {

        ProteinMutation pm = format.parse("p.T399delinsL");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(399, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1() throws Exception {

        ProteinMutation pm = format.parse("p.L330_A331delinsF");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(330, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(331, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[])  pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMulti() throws Exception {

        ProteinMutation pm = format.parse("p.D419_R420delinsSSDG");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(419, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(420, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseNonStandardCosmic() throws Exception {

        /*
        "p.K487_L498del12"
        "p.P564_L567delPRAL"
        "p.T399>L"
         */

    }
/*

    @Test
    public void testAsHGVMutationFormats() throws Exception {

        Map<String, String> hgvFormats = new HashMap<>();

        hgvFormats.put("p.R54C", "p.Arg54Cys");
        hgvFormats.put("p.E3815*", "p.Glu3815Ter");
        hgvFormats.put("p.I6616del", "p.Ile6616del");
        hgvFormats.put("p.K487_L498del12", "p.Lys487_Leu498del");
        hgvFormats.put("p.P564_L567delPRAL", "p.Pro564_Leu567del");
        hgvFormats.put("p.M682fs*1", "p.Met682fsTer1");
        hgvFormats.put("p.S1476fs*>9", "p.Ser1476fsTer9");
        hgvFormats.put("p.T399>L", "p.Thr399>Leu");
        hgvFormats.put("p.L330_A331>F", "p.Leu330_Ala331>Phe");
        hgvFormats.put("p.W39_E40>*", "p.Trp39_Glu40>Ter");
        hgvFormats.put("p.D419_R420>SSDG", "p.Asp419_Arg420>SerSerAspGly");

        for (Map.Entry<String, String> hgvFormat : hgvFormats.entrySet()) {
            Assert.assertEquals(hgvFormat.getValue(), ProteinMutationHGVFormat.asHGVMutationFormat(hgvFormat.getKey()));
        }
    }*/
}