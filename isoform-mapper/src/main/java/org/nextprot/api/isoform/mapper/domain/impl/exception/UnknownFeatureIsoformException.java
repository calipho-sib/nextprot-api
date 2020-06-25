package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

import java.util.stream.Collectors;

public class UnknownFeatureIsoformException extends FeatureQueryException {

    private static final String UNKNOWN_ISOFORM = "unknownIsoform";
    private static final String EXPECTED_ISOFORMS = "expectedIsoforms";

    public UnknownFeatureIsoformException(Entry entry, SingleFeatureQuery query, String unknownIsoform) {

        super(query);

        getReason().addCause(UNKNOWN_ISOFORM, unknownIsoform);
        getReason().addCause(EXPECTED_ISOFORMS, entry.getIsoforms().stream().map(iso -> iso.getMainEntityName().getName()).collect(Collectors.toList()));

        getReason().setMessage("unknown isoform: cannot find isoform "+unknownIsoform+" in entry "+ query.getAccession()
                + " (existing isoforms: "+ getReason().getCause(EXPECTED_ISOFORMS)+")");
    }

    public UnknownFeatureIsoformException(SingleFeatureQuery query, String unknownIsoform) {

        super(query);

        getReason().addCause(UNKNOWN_ISOFORM, unknownIsoform);

        getReason().setMessage("unknown isoform: cannot find isoform "+unknownIsoform+" in entry "+ query.getAccession()
                + " (existing isoforms: "+ getReason().getCause(EXPECTED_ISOFORMS)+")");
    }
}
