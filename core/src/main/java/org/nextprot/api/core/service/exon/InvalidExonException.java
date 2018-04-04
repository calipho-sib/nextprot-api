package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.exon.UncategorizedExon;

public abstract class InvalidExonException extends Exception {

    public InvalidExonException(UncategorizedExon exon, String message) {

        super("Invalid exon mapping to gene "+exon.getGeneRegion().getGeneName()+": message="+message);
    }
}
