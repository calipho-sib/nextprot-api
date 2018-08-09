package org.nextprot.api.isoform.mapper.domain.impl.exception;

public class UnknownIsoformException extends Exception {

    private final String unknownIsoform;

    public UnknownIsoformException(String unknownIsoform) {

        super("Cannot find neXtProt entry "+ unknownIsoform);
        this.unknownIsoform = unknownIsoform;
    }

    public String getUnknownIsoformAccession() {
        return unknownIsoform;
    }
}
