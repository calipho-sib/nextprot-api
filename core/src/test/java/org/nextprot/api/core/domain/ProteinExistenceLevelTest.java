package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class ProteinExistenceLevelTest {

    @Test
    public void valueOfString() {

        Assert.assertEquals(ProteinExistenceLevel.PROTEIN_LEVEL, ProteinExistenceLevel.valueOfKey("protein level"));
    }

    @Test
    public void valueOfString2() {

        Assert.assertEquals(ProteinExistenceLevel.PROTEIN_LEVEL, ProteinExistenceLevel.valueOfKey("Evidence at protein level"));
    }

    @Test
    public void valueOfString3() {

        Assert.assertEquals(ProteinExistenceLevel.PROTEIN_LEVEL, ProteinExistenceLevel.valueOfKey("Evidence_at_protein_level"));
    }
}