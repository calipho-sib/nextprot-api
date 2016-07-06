package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import com.nextprot.api.isoform.mapper.service.FeatureValidator;
import com.nextprot.api.isoform.mapper.utils.EntryIsoform;

/**
 * Validate PTM type feature
 *
 * Created by fnikitin on 05/07/16.
 */
class PtmValidator implements FeatureValidator {

    @Override
    public FeatureQueryResult validate(FeatureQuery query, EntryIsoform entryIsoform) throws FeatureQueryException {


        return null;
    }
}
