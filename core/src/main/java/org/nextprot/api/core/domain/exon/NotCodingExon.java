package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

public class NotCodingExon extends CategorizedExon {

	private static final long serialVersionUID = 1L;

	public NotCodingExon(Exon exon, ExonCategory exonCategory) {

		super(exon, exonCategory);
	}

	@Override
	public GeneRegion getCodingGeneRegion() {

	    return null;
    }
}

