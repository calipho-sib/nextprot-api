package org.nextprot.api.commons.bio.variation;

import org.junit.Test;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariantHGVSFormat;

public class SequenceChangeOperationTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        SequenceChangeOperation operation = new SequenceChangeOperation();

        SequenceVariation duplication = new SequenceVariantHGVSFormat().parse("p.Leu3_Met6dup");

        String transformedSequence = operation.transform("KMLISMPEPTIDE", duplication);
        System.out.println(transformedSequence);
    }
}