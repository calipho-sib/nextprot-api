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

    @Test(expected = IllegalArgumentException.class)
    public void valueOfInvalidString() {

        ProteinExistence.valueOfKey("roudoudou");
    }

    @Test
    public void valueOfLevel() {

        Assert.assertEquals(ProteinExistence.PROTEIN_LEVEL, ProteinExistence.valueOfLevel(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfInvalidLevel() {

        ProteinExistence.valueOfLevel(10);
    }
}