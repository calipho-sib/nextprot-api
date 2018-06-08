package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

public class ExonMono extends CategorizedExon implements FirstCodingExon, LastCodingExon {

	private static final long serialVersionUID = 2L;

	private int startPosition;
	private int stopPosition;
    private GeneRegion codingGeneRegion;

	public ExonMono(Exon exon, int startPosition, int stopPosition) {

        super(exon, ExonCategory.MONO);
		this.startPosition = startPosition;
		this.stopPosition = stopPosition;
		this.codingGeneRegion = new GeneRegion(exon.getGeneRegion().getGeneName(), startPosition, stopPosition);
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

    @Override
	public int getStopPosition() {
		return stopPosition;
	}

    @Override
    public GeneRegion getCodingGeneRegion() {

        return codingGeneRegion;
    }
}
