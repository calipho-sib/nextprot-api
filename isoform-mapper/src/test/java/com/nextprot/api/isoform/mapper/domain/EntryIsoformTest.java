package com.nextprot.api.isoform.mapper.domain;

import com.nextprot.api.isoform.mapper.IsoformMappingBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class EntryIsoformTest extends IsoformMappingBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void testGetIsoformNumber() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertEquals(1, EntryIsoform.getIsoformNumber(entry.getIsoforms().get(0)));
    }

    @Test
    public void testGetIsoformByName() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Assert.assertEquals("NX_P01308-1", EntryIsoform.getIsoformByName(entry, "NX_P01308-1").getUniqueName());
    }

    @Test
    public void testGetOtherIsoforms() throws Exception {

        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").withTargetIsoforms());

        Isoform isoform = entry.getIsoforms().get(0);

        EntryIsoform entryIsoform = new EntryIsoform("NX_P01308", entry, isoform);

        Assert.assertTrue(entryIsoform.getOtherIsoforms().isEmpty());
    }
}