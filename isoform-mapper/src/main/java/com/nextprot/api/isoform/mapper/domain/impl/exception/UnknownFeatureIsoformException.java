package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.core.domain.Entry;

import java.util.stream.Collectors;

public class UnknownFeatureIsoformException extends FeatureQueryException {

    private static final String UNKNOWN_ISOFORM = "unknownIsoform";

    public UnknownFeatureIsoformException(Entry entry, FeatureQuery query, String unknownIsoform) {

        super(query);

        getError().addCause(UNKNOWN_ISOFORM, unknownIsoform);
        getError().setMessage("unknown isoform: cannot find isoform "+unknownIsoform+" in entry "+ query.getAccession()
                + " (existing isoforms: "+ entry.getIsoforms().stream().map(iso -> iso.getMainEntityName().getName()).collect(Collectors.toList())+")");
    }
}
