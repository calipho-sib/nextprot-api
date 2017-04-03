package org.nextprot.api.commons.bio.variation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariantHGVSFormat;

import java.util.Optional;

public class SequenceChangeOperatorTest {

    @Test
    public void testBuildSequenceVariantDuplication() throws Exception {

        assertOperation("KMLISMPEPTIDE", new SequenceVariantHGVSFormat().parse("p.Leu3_Met6dup"),
                6, 6, "M", "MLISM", "KMLISMLISMPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantSubstitution() throws Exception {

        assertOperation("KMLISMPEPTIDE", new SequenceVariantHGVSFormat().parse("p.Leu3Met"),
                3, 3, "L", "M", "KMMISMPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantInsertion() throws Exception {

        assertOperation("KMLISMRSPEPTIDE", new SequenceVariantHGVSFormat().parse("p.Arg7_Ser8insLeuIleSerMet"),
                7, 7, "R", "RLISM", "KMLISMRLISMSPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantDeletion() throws Exception {

        assertOperation("KMLISMRSPEPTIDE", new SequenceVariantHGVSFormat().parse("p.Leu3_Met6del"),
                3, 6, "LISM", "", "KMRSPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantDeletionInsertion() throws Exception {

        assertOperation("KMLIMLESMRSPEPTIDE", new SequenceVariantHGVSFormat().parse("p.Met5_Glu7delinsLeuIleSerMet"),
                5, 7, "MLE", "LISM", "KMLILISMSMRSPEPTIDE");
    }

    private static void assertOperation(String originalSequence, SequenceVariation sequenceVariation,
                                        int expectedPosStart, int expectedPosEnd, String expectedOriginal,
                                        String expectedVariant, String expectedVariantSequence) {

        Optional<SequenceChangeOperator> op = SequenceChangeOperator.findOperator(sequenceVariation.getSequenceChange());
        Assert.assertTrue(op.isPresent());

        Assert.assertEquals(expectedPosStart, op.get().selectPositionStart(sequenceVariation.getChangingSequence()));
        Assert.assertEquals(expectedPosEnd,  op.get().selectPositionEnd(sequenceVariation.getChangingSequence()));

        Assert.assertEquals(expectedOriginal, op.get().original(originalSequence, sequenceVariation.getChangingSequence()));
        Assert.assertEquals(expectedVariant, op.get().variant(originalSequence, sequenceVariation));
        Assert.assertEquals(expectedVariantSequence, op.get().buildVariantSequence(originalSequence, sequenceVariation));
    }
}