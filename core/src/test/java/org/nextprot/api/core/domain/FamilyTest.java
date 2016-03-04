package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class FamilyTest {

    @Test
    public void settingNameShouldSetLevelFamily() throws Exception {

        Family family = new Family();
        family.setName("bla family");

        Assert.assertEquals("Family", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevelSubfamily() throws Exception {

        Family family = new Family();
        family.setName("bla subfamily");

        Assert.assertEquals("Subfamily", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevelSuperfamily() throws Exception {

        Family family = new Family();
        family.setName("bla superfamily");

        Assert.assertEquals("Superfamily", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevelSubsubfamily() throws Exception {

        Family family = new Family();
        family.setName("GABRD sub-subfamily");

        Assert.assertEquals("Subsubfamily", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevelUnknown() throws Exception {

        Family family = new Family();
        family.setName("bla");

        Assert.assertEquals("", family.getLevel());
    }
}