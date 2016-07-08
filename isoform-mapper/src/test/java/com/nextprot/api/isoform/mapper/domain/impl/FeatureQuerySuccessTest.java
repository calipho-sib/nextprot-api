package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;

import static org.mockito.Mockito.when;

public class FeatureQuerySuccessTest {

    @Test
    public void testOnSuccess() throws FeatureQueryException {

        FeatureQuery query = new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName(), true);

        FeatureQuerySuccess result = new FeatureQuerySuccess(query);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-1", true), 1158, 1158);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-2", false), 1158, 1158);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-3", false), 1120, 1120);
    }

    public static EntryIsoform mockEntryIsoform(String accession) {

        EntryIsoform entryIsoform = Mockito.mock(EntryIsoform.class);

        Isoform isoform = mockIsoform(accession, true);
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