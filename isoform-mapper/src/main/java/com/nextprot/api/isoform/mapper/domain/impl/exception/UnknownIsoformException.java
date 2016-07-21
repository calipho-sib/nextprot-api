package com.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.core.domain.Entry;

public class UnknownIsoformException extends RuntimeException {

    private final String unknownIsoformName;

    public UnknownIsoformException(String unknownIsoformName, Entry entry) {

        super("unknown isoform: " + unknownIsoformName + " not found in entry " + entry.getUniqueName());

        this.unknownIsoformName = unknownIsoformName;
    }

    public String getUnknownIsoformName() {
        return unknownIsoformName;
    }
}
