package org.nextprot.api.isoform.mapper.domain.impl.exception;

public class UnknownGeneNameException extends PreIsoformParseException {

    private final String geneName;

    public UnknownGeneNameException(String geneName) {

        super("Cannot find a neXtProt entry associated with gene name "+ geneName);
        this.geneName = geneName;
    }

    public String getGeneName() {
        return geneName;
    }
}
