package org.nextprot.api.etl.service.impl;

import org.mockito.Mockito;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;

import java.text.ParseException;

import static org.nextprot.api.etl.statement.StatementETLBaseUnitTest.mockIsoform;

public class SequenceFeatureFactoryServiceMockImpl implements SequenceFeatureFactoryService {

    private final String isoformAccession;

    public SequenceFeatureFactoryServiceMockImpl(String isoformAccession) {

        this.isoformAccession = isoformAccession;
    }

    @Override
    public SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException {

        SequenceFeature sequenceFeature = Mockito.mock(SequenceFeature.class);

        Isoform isoform = mockIsoform(isoformAccession, "", false);

        Mockito.when(sequenceFeature.getIsoform()).thenReturn(isoform);

        return sequenceFeature;
    }

    @Override
    public SequenceFeature newSequenceFeature(SingleFeatureQuery query) throws FeatureQueryException {
        return null;
    }
}