package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.Exon;

public abstract class InvalidExonException extends Exception {

    public InvalidExonException(Exon exon, String message) {

        super("Invalid exon mapping to gene "+exon.getGeneRegion().getGeneName()+": message="+message);
    }
}
