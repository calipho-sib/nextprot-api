package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;

public class OutOfBoundSequencePositionException extends FeatureQueryException {

    static final String SEQUENCE_POS = "sequencePosition";

    public OutOfBoundSequencePositionException(SingleFeatureQuery query, int position) {

        super(query);

        getReason().setMessage("out of bound sequence position: position " + position + " of " + query.getAccession()+" sequence");
        getReason().addCause(SEQUENCE_POS, position);
    }
}
