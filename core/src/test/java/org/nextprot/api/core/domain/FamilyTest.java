package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class FamilyTest {

    @Test
    public void settingNameShouldSetLevel() throws Exception {

        Family family = new Family();
        family.setName("bla family");

        Assert.assertEquals("Family", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevel2() throws Exception {

        Family family = new Family();
        family.setName("bla subfamily");

        Assert.assertEquals("Subfamily", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevel3() throws Exception {

        Family family = new Family();
        family.setName("bla superfamily");

        Assert.assertEquals("Superfamily", family.getLevel());
    }

    @Test
    public void settingNameShouldSetLevel4() throws Exception {

        Family family = new Family();
        family.setName("bla");

        Assert.assertEquals("", family.getLevel());
    }
}