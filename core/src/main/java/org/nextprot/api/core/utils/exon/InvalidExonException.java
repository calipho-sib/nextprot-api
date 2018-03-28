package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.GenericExon;

public abstract class InvalidExonException extends Exception {

    public InvalidExonException(GenericExon exon, String message) {

        super("Invalid exon mapping to gene "+exon.getGeneRegion().getGeneName()+": message="+message);
    }
}
