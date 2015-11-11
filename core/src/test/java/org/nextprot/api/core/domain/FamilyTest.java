package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

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

    @Test
    public void testGetHierarchyFromRoot() throws Exception {

        Family superfamily = new Family();
        superfamily.setName("ba superfamily");

        Family family = new Family();
        family.setName("be family");

        Family subfamily = new Family();
        subfamily.setName("bi subfamily");

        subfamily.setParent(family);
        family.setParent(superfamily);

        Assert.assertEquals(Arrays.asList(superfamily, family, subfamily), subfamily.getHierarchyFromRoot());
    }
}