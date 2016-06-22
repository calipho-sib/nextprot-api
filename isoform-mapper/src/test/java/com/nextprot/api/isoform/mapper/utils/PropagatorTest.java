package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropagatorTest {

    @Test
    public void testGetOtherIsoforms() throws Exception {

        Entry entry = mock(Entry.class);

        List<Isoform> isoforms = Arrays.asList(
                mockIsoform("NX_Q9UI33-1"),
                mockIsoform("NX_Q9UI33-2"),
                mockIsoform("NX_Q9UI33-3")
        );

        when(entry.getIsoforms()).thenReturn(isoforms);

        Propagator propagator = new Propagator(entry);
        List<Isoform> others = propagator.getOtherIsoforms(mockIsoform("NX_Q9UI33-1"));
        Assert.assertEquals(2, others.size());
        for (Isoform isoform : others) {
            Assert.assertTrue(
                    isoform.getUniqueName().equals("NX_Q9UI33-2") ||
                    isoform.getUniqueName().equals("NX_Q9UI33-3") );
        }
    }

    private Isoform mockIsoform(String name) {

        Isoform iso = mock(Isoform.class);
        when(iso.getUniqueName()).thenReturn(name);
        return iso;
    }
}