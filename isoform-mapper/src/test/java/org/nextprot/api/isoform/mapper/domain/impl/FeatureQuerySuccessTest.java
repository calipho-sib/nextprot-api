package org.nextprot.api.isoform.mapper.domain.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccessImpl;
import org.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;

import java.text.ParseException;

import static org.nextprot.api.core.domain.EntryUtilsTest.mockEntry;
import static org.nextprot.api.core.domain.EntryUtilsTest.mockIsoform;

public class FeatureQuerySuccessTest {

    @Test
    public void testOnSuccess() throws FeatureQueryException, ParseException {

        FeatureQuery query = new FeatureQuery("NX_Q9UI33", "SCN11A-p.Leu1158Pro", AnnotationCategory.VARIANT.getApiTypeName());

        SequenceVariant sequenceVariant = new SequenceVariant("SCN11A-p.Leu1158Pro");

        Entry entry = mockEntry("NX_Q9UI33",
                mockIsoform("NX_Q9UI33-1", "Iso 1", true), mockIsoform("NX_Q9UI33-2", "Iso 2", false),
                mockIsoform("NX_Q9UI33-3", "Iso 3", false), mockIsoform("NX_Q9UI33-4", "Iso 4", false));

        FeatureQuerySuccessImpl result = new FeatureQuerySuccessImpl(entry, query, sequenceVariant);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-1", "Iso 1", true), 1158, 1158);
        result.addMappedFeature(mockIsoform("NX_Q9UI33-3", "Iso 3", false), 1120, 1120);
        result.addUnmappedFeature(mockIsoform("NX_Q9UI33-4", "Iso 4", false));

        Assert.assertEquals("SCN11A-iso1-p.Leu1158Pro", result.getData().get("NX_Q9UI33-1").getIsoSpecificFeature());
        Assert.assertEquals("SCN11A-iso3-p.Leu1120Pro", result.getData().get("NX_Q9UI33-3").getIsoSpecificFeature());
        Assert.assertNull(result.getData().get("NX_Q9UI33-4").getIsoSpecificFeature());
    }
}