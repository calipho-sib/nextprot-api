package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.GeneRegion;

public class ExonStop extends CategorizedExon implements LastCodingExon {

	private static final long serialVersionUID = 2L;

	private int stopPosition;
    private GeneRegion codingGeneRegion;

	public ExonStop(Exon exon, int stopPosition) {

        super(exon, ExonCategory.STOP);
		this.stopPosition = stopPosition;
        this.codingGeneRegion = new GeneRegion(exon.getGeneRegion().getGeneName(),
                exon.getGeneRegion().getFirstPosition(), stopPosition);
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
