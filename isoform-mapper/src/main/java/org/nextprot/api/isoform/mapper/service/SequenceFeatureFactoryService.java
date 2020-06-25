package org.nextprot.api.isoform.mapper.service;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.feature.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

import java.text.ParseException;

public interface SequenceFeatureFactoryService {

    SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException;

    SequenceFeature newSequenceFeature(SingleFeatureQuery query) throws FeatureQueryException;
}
