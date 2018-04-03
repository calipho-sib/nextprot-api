package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.utils.exon.ExonCategory;

import java.io.Serializable;

public class ExonStop extends CategorizedExon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int stopPosition;

	public ExonStop() {
        super(ExonCategory.STOP);
    }

	public ExonStop(int stopPosition) {

	    this();
		this.stopPosition = stopPosition;
	}

	public int getStopPosition() {
		return stopPosition;
	}
}
