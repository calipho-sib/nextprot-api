package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;

import static org.mockito.Mockito.when;

public class FeatureQuerySuccessTest {

    @Test
    public void testOnSuccess() throws FeatureQueryException, ParseException {

        FeatureQuery query = new FeatureQuery(mockEntryIsoform("NX_Q9UI33", "NX_Q9UI33-1"), "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        SequenceVariant sequenceVariant = new SequenceVariant("SCN11A-p.Leu1158Pro");

        FeatureQuerySuccess result = new FeatureQuerySuccess(query, sequenceVariant);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-1", true), 1158, 1158);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-3", false), 1120, 1120);
        result.addUnmappedFeature(mockIsoform("NX_Q9UI33-4", false));

        Assert.assertEquals("SCN11A-iso1-p.Leu1158Pro", result.getData().get("NX_Q9UI33-1").getIsoSpecificFeature());
        Assert.assertEquals("SCN11A-iso3-p.Leu1120Pro", result.getData().get("NX_Q9UI33-3").getIsoSpecificFeature());
        Assert.assertNull(result.getData().get("NX_Q9UI33-4").getIsoSpecificFeature());
    }

    public static EntryIsoform mockEntryIsoform(String accession, String isoAccession) {

        EntryIsoform entryIsoform = Mockito.mock(EntryIsoform.class);

        Isoform isoform = mockIsoform(isoAccession, true);
        when(entryIsoform.getIsoform()).thenReturn(isoform);
        when(entryIsoform.getAccession()).thenReturn(accession);

        return entryIsoform;
    }

    public static Isoform mockIsoform(String accession, boolean canonical) {

        Isoform isoform = Mockito.mock(Isoform.class);
        when(isoform.getUniqueName()).thenReturn(accession);
        when(isoform.isCanonicalIsoform()).thenReturn(canonical);
        return isoform;
    }
}