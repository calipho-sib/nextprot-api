package org.nextprot.api.etl.service.impl;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.service.SequenceFeatureFactoryService;

import java.text.ParseException;

public class SequenceFeatureFactoryServiceMockImpl implements SequenceFeatureFactoryService {

    @Override
    public SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException {

        return null;
    }

    @Override
    public SequenceFeature newSequenceFeature(SingleFeatureQuery query) throws FeatureQueryException {
        return null;
    }
}