package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.GenericExon;

public class ExonInvalidBoundException extends InvalidExonException {

    public ExonInvalidBoundException(GenericExon exon) {

        super(exon, "invalid bounds ["+exon.getFirstPositionOnGene()+"-"+exon.getLastPositionOnGene()+"]");
    }
}
