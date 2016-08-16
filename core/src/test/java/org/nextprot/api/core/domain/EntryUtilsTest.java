package org.nextprot.api.core.domain;

import org.mockito.Mockito;
import org.nextprot.api.core.dao.EntityName;

import java.util.Arrays;

import static org.mockito.Mockito.when;

public class EntryUtilsTest {

    public static Entry mockEntry(String accession, Isoform... isoforms) {

        Entry entry = Mockito.mock(Entry.class);

        when(entry.getUniqueName()).thenReturn(accession);

        if (isoforms.length > 0) {
            when(entry.getIsoforms()).thenReturn(Arrays.asList(isoforms));
        }

        return entry;
    }

    public static Isoform mockIsoform(String accession, String name, boolean canonical) {

        Isoform isoform = Mockito.mock(Isoform.class);
        when(isoform.getUniqueName()).thenReturn(accession);
        when(isoform.isCanonicalIsoform()).thenReturn(canonical);

        EntityName entityName = Mockito.mock(EntityName.class);
        when(entityName.getName()).thenReturn(name);

        when(isoform.getMainEntityName()).thenReturn(entityName);

        return isoform;
    }
}