package org.nextprot.api.isoform.mapper.service;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;

import java.text.ParseException;

public interface SequenceFeatureFactoryService {

    SequenceFeature newSequenceFeature(String featureName, String featureType) throws ParseException, SequenceVariationBuildException;

    SequenceFeature newSequenceFeature(SingleFeatureQuery query) throws FeatureQueryException;
}
