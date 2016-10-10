package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.core.domain.Entry;

public class UnknownIsoformRuntimeException extends RuntimeException {

    private final String unknownIsoformName;

    public UnknownIsoformRuntimeException(String unknownIsoformName, Entry entry) {

        super("unknown isoform: " + unknownIsoformName + " not found in entry " + entry.getUniqueName());

        this.unknownIsoformName = unknownIsoformName;
    }

    public String getUnknownIsoformName() {
        return unknownIsoformName;
    }
}
