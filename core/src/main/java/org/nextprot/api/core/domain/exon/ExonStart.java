package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

public class ExonStart extends CategorizedExon implements FirstCodingExon {

	private static final long serialVersionUID = 2L;

	private int startPosition;
    private GeneRegion codingGeneRegion;

	public ExonStart(Exon exon, int startPosition) {

        super(exon, ExonCategory.START);
		this.startPosition = startPosition;
        this.codingGeneRegion = new GeneRegion(exon.getGeneRegion().getGeneName(), startPosition, exon.getGeneRegion().getLastPosition());
    }

	@Override
	public int getStartPosition() {
		return startPosition;
	}

    @Override
    public GeneRegion getCodingGeneRegion() {

        return codingGeneRegion;
    }
}
