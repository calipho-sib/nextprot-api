package org.nextprot.api.core.domain.exon;

public class ExonStop extends CategorizedExon {

	private static final long serialVersionUID = 1L;

	private int stopPosition;

	public ExonStop(Exon exon) {
        super(exon, ExonCategory.STOP);
    }

	public ExonStop(Exon exon, int stopPosition) {

	    this(exon);
		this.stopPosition = stopPosition;
	}

	public int getStopPosition() {
		return stopPosition;
	}
}
