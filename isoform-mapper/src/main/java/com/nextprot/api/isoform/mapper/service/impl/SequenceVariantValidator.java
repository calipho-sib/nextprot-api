package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.EntryIsoform;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import com.nextprot.api.isoform.mapper.domain.impl.exception.IncompatibleIsoformException;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;

class SequenceVariantValidator extends SequenceFeatureValidator {

    SequenceVariantValidator(FeatureQuery query) {
        super(query);
    }

    @Override
    protected SequenceFeature newSequenceFeature(String feature) throws ParseException {

        return new SequenceVariant(feature);
    }

    protected void checkFeatureIsoformName(SequenceFeature sequenceFeature) throws IncompatibleIsoformException {

        Isoform isoformRefByFeature = getIsoform(sequenceFeature.getRawIsoformName());

        if (!isoformRefByFeature.getUniqueName().equals(query.getEntryIsoform().getIsoform().getUniqueName())) {

            throw new IncompatibleIsoformException(query, isoformRefByFeature.getUniqueName());
        }
    }

    private Isoform getIsoform(String featureIsoformName) {

        Entry entry = query.getEntryIsoform().getEntry();

        if (featureIsoformName.isEmpty()) {
            return EntryIsoform.getCanonicalIsoform(entry);
        }

        return EntryIsoform.getIsoformByName(entry, featureIsoformName);
    }
}
