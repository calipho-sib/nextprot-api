package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.exon.SimpleExon;

public class ExonInvalidBoundException extends InvalidExonException {

    public ExonInvalidBoundException(SimpleExon exon) {

        super(exon, "invalid bounds ["+exon.getFirstPositionOnGene()+"-"+exon.getLastPositionOnGene()+"]");
    }
}
