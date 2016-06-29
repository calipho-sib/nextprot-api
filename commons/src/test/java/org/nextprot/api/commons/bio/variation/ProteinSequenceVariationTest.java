package org.nextprot.api.commons.bio.variation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcid;

public class ProteinSequenceVariationTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Arginine, 54).substitutedBy(AminoAcid.Cysteine).build();

        Assert.assertEquals(AminoAcid.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(!pm.isAminoAcidRange());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcid.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Lysine, 73).deleted().build();

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Lysine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(73, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 487, AminoAcid.Leucine, 498).deleted().build();

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals(AminoAcid.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(682, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcid.Threonine, 399).deletedAndInserts(AminoAcid.Leucine).build();

        Assert.assertEquals(AminoAcid.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Threonine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(399, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcid[]{AminoAcid.Leucine}, (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Leucine, 330, AminoAcid.Alanine, 331).deletedAndInserts(AminoAcid.Phenylalanine).build();

        Assert.assertEquals(AminoAcid.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcid[] { AminoAcid.Phenylalanine }, (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.AsparticAcid, 419, AminoAcid.Arginine, 420).deletedAndInserts(AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine).build();

        Assert.assertEquals(AminoAcid.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcid[] { AminoAcid.Serine, AminoAcid.Serine, AminoAcid.AsparticAcid, AminoAcid.Glycine }, (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildInsertion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcid.Lysine, 2, AminoAcid.Methionine, 3).inserts(AminoAcid.Glutamine, AminoAcid.Serine, AminoAcid.Lysine).build();

        Assert.assertEquals(AminoAcid.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(2, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcid.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(3, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertEquals(2, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
        Assert.assertArrayEquals(new AminoAcid[] { AminoAcid.Glutamine, AminoAcid.Serine, AminoAcid.Lysine }, (AminoAcid[]) pm.getProteinSequenceChange().getValue());
    }
}