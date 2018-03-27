package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.Exon;

public class ExonInvalidBoundException extends InvalidExonException {

    public ExonInvalidBoundException(Exon exon) {

        super(exon, "invalid bounds ["+exon.getFirstPositionOnGene()+"-"+exon.getLastPositionOnGene()+"]");
    }
}
