package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

public class CodingExon extends CategorizedExon {

	private static final long serialVersionUID = 1L;

	public CodingExon(Exon exon) {

		super(exon, ExonCategory.CODING);
	}

	public GeneRegion getCodingGeneRegion() {

	    return getGeneRegion();
    }
}

