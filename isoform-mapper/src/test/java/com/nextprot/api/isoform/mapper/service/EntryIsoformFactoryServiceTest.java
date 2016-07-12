package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Isoform;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EntryIsoformFactoryServiceTest extends IsoformMappingBaseTest {

    @Autowired
    private EntryIsoformFactoryService entryIsoformFactoryService;

    @Test
    public void testCanonicalIsoform() throws Exception {

        EntryIsoform entryIsoform = entryIsoformFactoryService.createsEntryIsoform("NX_Q9UI33");

        Assert.assertEquals("NX_Q9UI33", entryIsoform.getAccession());
        Assert.assertEquals("NX_Q9UI33-1", entryIsoform.getIsoform().getUniqueName());
        Assert.assertTrue(entryIsoform.isCanonicalIsoform());
    }

    @Test
    public void testSpecifiedIsoform() throws Exception {

        EntryIsoform entryIsoform = entryIsoformFactoryService.createsEntryIsoform("NX_Q9UI33-2");

        Assert.assertEquals("NX_Q9UI33-2", entryIsoform.getIsoform().getUniqueName());
        Assert.assertTrue(!entryIsoform.isCanonicalIsoform());
    }

    @Test
    public void testGetOtherIsoforms() throws Exception {

        EntryIsoform entryIsoform = entryIsoformFactoryService.createsEntryIsoform("NX_Q9UI33");

        List<Isoform> others = entryIsoform.getOtherIsoforms();
        Assert.assertEquals(2, others.size());
        for (Isoform isoform : others) {
            Assert.assertTrue(
                    isoform.getUniqueName().equals("NX_Q9UI33-2") ||
                            isoform.getUniqueName().equals("NX_Q9UI33-3") );
        }
    }
}