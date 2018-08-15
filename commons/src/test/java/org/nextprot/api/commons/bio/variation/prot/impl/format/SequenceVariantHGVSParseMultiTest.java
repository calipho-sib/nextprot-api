package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;

import java.text.ParseException;

public class SequenceVariantHGVSParseMultiTest {

    VariantHGVSFormat format = new VariantHGVSFormat();

    @Ignore
    @Test
    public void testParseMultisVariants() throws ParseException {
        /*
MULTIS:
p.(=,Ile411_Gly426del)
         */
        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.(=,Ile411_Gly426del)");
    }
}