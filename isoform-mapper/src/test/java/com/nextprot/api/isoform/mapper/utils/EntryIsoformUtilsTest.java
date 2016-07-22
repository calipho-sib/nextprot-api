package com.nextprot.api.isoform.mapper.utils;

import com.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EntryIsoformUtilsTest extends IsoformMappingBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void testGetIsoformByAccession() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertEquals("NX_P01308-1", EntryIsoformUtils.getIsoformByName(entry, "NX_P01308-1").getUniqueName());
    }

    @Test
    public void testGetIsoformByName() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P06213").withTargetIsoforms());

        Assert.assertEquals("NX_P06213-1", EntryIsoformUtils.getIsoformByName(entry, "Long").getUniqueName());
    }

    @Test
    public void testGetOtherIsoforms() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Isoform isoform = entry.getIsoforms().get(0);

        Assert.assertTrue(EntryIsoformUtils.getOtherIsoforms(entry, isoform.getUniqueName()).isEmpty());
    }

    @Test
    public void testGetOtherIsoforms2() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_Q9UI33").withTargetIsoforms());

        List<Isoform> others = EntryIsoformUtils.getOtherIsoforms(entry, "NX_Q9UI33-1");
        Assert.assertEquals(2, others.size());
        for (Isoform isoform : others) {
            Assert.assertTrue(
                    isoform.getUniqueName().equals("NX_Q9UI33-2") ||
                            isoform.getUniqueName().equals("NX_Q9UI33-3") );
        }
    }

    @Test
    public void testGetCanonicalIsoform() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertNotNull(EntryIsoformUtils.getCanonicalIsoform(entry));
        Assert.assertEquals("NX_P01308-1", EntryIsoformUtils.getCanonicalIsoform(entry).getUniqueName());
    }

    @Test
    public void testGetIsoformByNameLowerCase() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P06213").withTargetIsoforms());

        Assert.assertEquals("NX_P06213-1", EntryIsoformUtils.getIsoformByName(entry, "long").getUniqueName());
    }
}