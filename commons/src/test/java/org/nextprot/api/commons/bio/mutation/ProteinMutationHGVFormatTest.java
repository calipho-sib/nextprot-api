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
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[]) pm.getMutation().getValue());
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
    public void testParseSubstitutionCode3() throws Exception {

        ProteinMutation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(54, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(54, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getMutation().getValue());
    }

    @Test
    public void testParseAADeletionCode3() throws Exception {

        ProteinMutation pm = format.parse("p.Lys73del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(73, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseRangeDeletionCode3() throws Exception {

        ProteinMutation pm = format.parse("p.Lys487_Leu498del");

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(487, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(498, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseFrameshiftCode3() throws Exception {

        ProteinMutation pm = format.parse("p.Met682fsTer1");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(682, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Frameshift);
        Assert.assertEquals(1, pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletion1AaAndInsertion1Code3() throws Exception {

        ProteinMutation pm = format.parse("p.Thr399delinsLeu");

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(399, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletionMultiAasAndInsertion1Code3() throws Exception {

        ProteinMutation pm = format.parse("p.Leu330_Ala331delinsPhe");

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(330, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(331, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[])  pm.getMutation().getValue());
    }

    @Test
    public void testParseDeletionMultiAndInsertionMultiCode3() throws Exception {

        ProteinMutation pm = format.parse("p.Asp419_Arg420delinsSerSerAspGly");

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(419, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(420, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        ProteinMutation pm = format.parse("p.R54C", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(54, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(54, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getMutation().getValue());
    }

    @Test
    public void testParseAANonStandardDeletion1() throws Exception {

        ProteinMutation pm = format.parse("p.K487_L498del12", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(487, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(498, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardDeletion2() throws Exception {

        ProteinMutation pm = format.parse("p.K487_L498delPRAL", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(487, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(498, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Deletion);
    }

    @Test
    public void testParseAANonStandardFrameshift() throws Exception {

        ProteinMutation pm = format.parse("p.S1476fs*>9", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Serine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(1476, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof Frameshift);
        Assert.assertEquals(9, pm.getMutation().getValue());
    }

    @Test
    public void testParseAANonStandardDelins1() throws Exception {

        ProteinMutation pm = format.parse("p.T399>L", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(399, pm.getFirstAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("L"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseAANonStandardDelins2() throws Exception {

        ProteinMutation pm = format.parse("p.L330_A331>F", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(330, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(331, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("F"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseAANonStandardDelins3() throws Exception {

        ProteinMutation pm = format.parse("p.W39_E40>*", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Tryptophan, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(39, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(40, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("*"), (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testParseAANonStandardDelins4() throws Exception {

        ProteinMutation pm = format.parse("p.D419_R420>SSDG", ProteinMutationHGVFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(419, pm.getFirstAffectedAminoAcidPos());
        Assert.assertEquals(420, pm.getLastAffectedAminoAcidPos());
        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(AminoAcidCode.valueOfCodeSequence("SSDG"), (AminoAcidCode[]) pm.getMutation().getValue());
    }
}