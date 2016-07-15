package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.SequenceModification;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

import java.text.ParseException;

class SequencePtmValidator extends SequenceFeatureValidator {

    SequencePtmValidator(FeatureQuery query) {
        super(query);
    }

    @Override
    protected SequenceFeature newSequenceFeature(String feature) throws ParseException {

        return new SequenceModification(feature);
    }

    @Override
    protected void doMoreChecks(SequenceVariation sequenceVariation) throws FeatureQueryException {

        // TODO: implementing validations with rules
    }
}
