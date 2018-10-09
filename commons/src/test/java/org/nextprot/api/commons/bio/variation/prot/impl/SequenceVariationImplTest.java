package org.nextprot.api.commons.bio.variation.prot.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.*;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

public class SequenceVariationImplTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.ARGININE, 54).thenSubstituteWith(AminoAcidCode.CYSTEINE).build();

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(54, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(!pm.getVaryingSequence().isMultipleAminoAcids());

        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.LYSINE, 73).thenDelete().build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(73, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(73, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 487, AminoAcidCode.LEUCINE, 498).thenDelete().build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(487, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(498, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 2).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(682, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(682, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Frameshift);
        Assert.assertEquals(AminoAcidCode.ALANINE, ((Frameshift.Change)pm.getSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFrameshiftBadStopPos() throws Exception {

        new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 1).build();
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.THREONINE, 399).thenDeleteAndInsert(AminoAcidCode.LEUCINE).build();

        Assert.assertEquals(AminoAcidCode.THREONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(399, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.THREONINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(399, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.LEUCINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LEUCINE, 330, AminoAcidCode.ALANINE, 331).thenDeleteAndInsert(AminoAcidCode.PHENYLALANINE).build();

        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(330, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ALANINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(331, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.PHENYLALANINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.ASPARTIC_ACID, 419, AminoAcidCode.ARGININE, 420).thenDeleteAndInsert(AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE).build();

        Assert.assertEquals(AminoAcidCode.ASPARTIC_ACID, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(419, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(420, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildInsertion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 2, AminoAcidCode.METHIONINE, 3).thenInsert(AminoAcidCode.GLUTAMINE, AminoAcidCode.SERINE, AminoAcidCode.LYSINE).build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(2, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(3, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertEquals(2, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.GLUTAMINE, AminoAcidCode.SERINE, AminoAcidCode.LYSINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDuplication() throws Exception {

        /*
        p.Ala3_Ser5dup (several amino acids): a duplication of amino acids Ala3 to Ser5 in the sequence MetGlyAlaArgSerSerHis to MetGlyAlaArgSerAlaArgSerSerHis
         */

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.ALANINE, 3, AminoAcidCode.SERINE, 5).thenDuplicate().build();

        Assert.assertEquals(AminoAcidCode.ALANINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(3, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.SERINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(5, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Duplication);
        Assert.assertEquals(5, ((Duplication)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testBuildPtm() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 123)
                .thenAddModification(UniProtPTM.S_NITROSYLATION()).build();

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(123, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(123, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertEquals("PTM-0280", pm.getSequenceChange().getValue());
    }

    // p.Met1ext-5
    @Test
    public void testBuildExtensionInit1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-5, AminoAcidCode.METHIONINE).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionInitiation);
        Assert.assertEquals(-5, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.METHIONINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    // p.Met1Valext-12
    @Test
    public void testBuildExtensionInit2() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-12, AminoAcidCode.VALINE).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionInitiation);
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    // p.Ter110GlnextTer17
    @Test
    public void testBuildExtensionTerm() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();

        Assert.assertEquals(AminoAcidCode.STOP, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(110, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.STOP, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(110, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionTermination);
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }

    @Test (expected = IllegalStateException.class)
    public void shouldNotBuildExtensionTermThatIsNotStop() throws Exception {

        new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.ASPARTIC_ACID, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();
    }

    @Test
    public void testBuildPtmFromAAs() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding()
                .fromAAs("WC")
                .selectAminoAcid(2)
                .thenAddModification(UniProtPTM.S_NITROSYLATION())
                .build();

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(2, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(2, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertEquals("PTM-0280", pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDuplicationFromAAs() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding()
                .fromAAs("PPAWS")
                .selectAminoAcidRange(3, 5)
                .thenDuplicate()
                .build();

        Assert.assertEquals(AminoAcidCode.ALANINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(3, pm.getVaryingSequence().getFirstAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.SERINE, pm.getVaryingSequence().getLastAminoAcid());
        Assert.assertEquals(5, pm.getVaryingSequence().getLastAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Duplication);
        Assert.assertEquals(5, ((Duplication)pm.getSequenceChange()).getInsertAfterPos());
    }
}