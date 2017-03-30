package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;

public class SequenceVariationHGVSParseMultiTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();

    @Ignore
    @Test
    public void testParseMultisVariants() throws ParseException {
        /*
MULTIS:
p.(=,Ile411_Gly426del)
         */
        format = new SequenceVariantHGVSFormat(SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);
        format.parse("p.(=,Ile411_Gly426del)");
    }
}