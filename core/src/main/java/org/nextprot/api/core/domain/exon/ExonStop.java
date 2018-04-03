package org.nextprot.api.core.domain.exon;

import java.io.Serializable;

public class ExonStop extends CategorizedExon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int stopPosition;

	public ExonStop() { super(); }

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
