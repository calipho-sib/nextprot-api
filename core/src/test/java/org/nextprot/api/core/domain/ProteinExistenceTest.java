package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class ProteinExistenceTest {

    @Test
    public void valueOfString() {

        Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, ProteinExistence.valueOfKey("protein level"));
    }

    @Test
    public void valueOfString2() {

        Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, ProteinExistence.valueOfKey("Evidence at protein level"));
    }

    @Test
    public void valueOfString3() {

        Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, ProteinExistence.valueOfKey("Evidence_at_protein_level"));
    }
}