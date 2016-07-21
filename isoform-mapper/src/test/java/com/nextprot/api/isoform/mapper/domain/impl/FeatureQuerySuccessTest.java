package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class FeatureQuerySuccessTest {

    @Test
    public void testOnSuccess() throws FeatureQueryException, ParseException {

        FeatureQuery query = new FeatureQuery(mockEntry("NX_Q9UI33",
                mockIsoform("NX_Q9UI33-1", "Iso 1", true), mockIsoform("NX_Q9UI33-2", "Iso 2", false),
                mockIsoform("NX_Q9UI33-3", "Iso 3", false), mockIsoform("NX_Q9UI33-4", "Iso 4", false)),
                "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        SequenceVariant sequenceVariant = new SequenceVariant("SCN11A-p.Leu1158Pro");

        FeatureQuerySuccess result = new FeatureQuerySuccess(query, sequenceVariant);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-1", "Iso 1", true), 1158, 1158);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-3", "Iso 3", false), 1120, 1120);
        result.addUnmappedFeature(mockIsoform("NX_Q9UI33-4", "Iso 4", false));

        Assert.assertEquals("SCN11A-iso1-p.Leu1158Pro", result.getData().get("NX_Q9UI33-1").getIsoSpecificFeature());
        Assert.assertEquals("SCN11A-iso3-p.Leu1120Pro", result.getData().get("NX_Q9UI33-3").getIsoSpecificFeature());
        Assert.assertNull(result.getData().get("NX_Q9UI33-4").getIsoSpecificFeature());
    }

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