package org.nextprot.api.commons.bio.mutation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Created by fnikitin on 09/07/15.
 */
public class ProteinMutationTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(54, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(54, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getMutation().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(73, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(73, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof Deletion);
        Assert.assertNull(pm.getMutation().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(487, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(498, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof Deletion);
        Assert.assertNull(pm.getMutation().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(682, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(682, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof Frameshift);
        Assert.assertEquals(1, pm.getMutation().getValue());
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(399, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(399, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Leucine}, (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(330, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(331, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Phenylalanine }, (AminoAcidCode[]) pm.getMutation().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        ProteinMutation pm = new ProteinMutation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstAffectedAminoAcidCode());
        Assert.assertEquals(419, pm.getFirstAffectedAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastAffectedAminoAcidCode());
        Assert.assertEquals(420, pm.getLastAffectedAminoAcidPos());

        Assert.assertTrue(pm.getMutation() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine }, (AminoAcidCode[]) pm.getMutation().getValue());
    }
}