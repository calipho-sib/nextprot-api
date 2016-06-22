package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.EntryBuilderService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntryIsoformTest {

    @Test
    public void testCanonicalIsoform() throws Exception {

        Entry entry = mockEntry("NX_Q9UI33", "NX_Q9UI33-1", "NX_Q9UI33-2", "NX_Q9UI33-3");

        EntryIsoform entryIsoform = EntryIsoform.parseAccession("NX_Q9UI33", mockEntryBuilderService(entry));
        Assert.assertEquals(entry, entryIsoform.getEntry());
        Assert.assertEquals("NX_Q9UI33-1", entryIsoform.getIsoform().getUniqueName());
        Assert.assertTrue(entryIsoform.isCanonicalIsoform());
    }

    @Test
    public void testSpecifiedIsoform() throws Exception {

        Entry entry = mockEntry("NX_Q9UI33", "NX_Q9UI33-1", "NX_Q9UI33-2", "NX_Q9UI33-3");

        EntryIsoform entryIsoform = EntryIsoform.parseAccession("NX_Q9UI33-2", mockEntryBuilderService(entry));
        Assert.assertEquals(entry, entryIsoform.getEntry());
        Assert.assertEquals("NX_Q9UI33-2", entryIsoform.getIsoform().getUniqueName());
        Assert.assertTrue(!entryIsoform.isCanonicalIsoform());
    }

    @Test
    public void testGetOtherIsoforms() throws Exception {

        Entry entry = mockEntry("NX_Q9UI33", "NX_Q9UI33-1", "NX_Q9UI33-2", "NX_Q9UI33-3");

        EntryIsoform entryIsoform = EntryIsoform.parseAccession("NX_Q9UI33", mockEntryBuilderService(entry));
        List<Isoform> others = entryIsoform.getOtherIsoforms();
        Assert.assertEquals(2, others.size());
        for (Isoform isoform : others) {
            Assert.assertTrue(
                    isoform.getUniqueName().equals("NX_Q9UI33-2") ||
                    isoform.getUniqueName().equals("NX_Q9UI33-3") );
        }
    }

    private EntryBuilderService mockEntryBuilderService(Entry entry) {

        EntryBuilderService builderService = mock(EntryBuilderService.class);
        when(builderService.build(any())).thenReturn(entry);
        return builderService;
    }

    private Entry mockEntry(String uniqueName, String... isoformNamesFirstCanonical) {

        Entry entry = mock(Entry.class);

        List<Isoform> isoforms = new ArrayList<>();

        isoforms.add(mockIsoform(isoformNamesFirstCanonical[0], true));
        for (int i=1 ; i<isoformNamesFirstCanonical.length ; i++) {
            isoforms.add(mockIsoform(isoformNamesFirstCanonical[i], false));
        }

        when(entry.getUniqueName()).thenReturn(uniqueName);
        when(entry.getIsoforms()).thenReturn(isoforms);

        return entry;
    }

    private Isoform mockIsoform(String name, boolean isCanonical) {

        Isoform iso = mock(Isoform.class);
        when(iso.getUniqueName()).thenReturn(name);
        when(iso.isCanonicalIsoform()).thenReturn(isCanonical);
        return iso;
    }
}