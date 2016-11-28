package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class OutOfBoundSequencePositionException extends FeatureQueryException {

    static final String SEQUENCE_POS = "sequencePosition";

    public OutOfBoundSequencePositionException(SingleFeatureQuery query, int position) {

        super(query);

        getError().setMessage("out of bound sequence position: position " + position + " of " + query.getAccession()+" sequence");
        getError().addCause(SEQUENCE_POS, position);
    }
}
