package org.nextprot.api.core.domain.exon;

import java.io.Serializable;

public class ExonStart extends CategorizedExon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int startPosition;

	public ExonStart() { super(); }

	public ExonStart(Exon exon) {

		super(exon, ExonCategory.START);
	}

	public ExonStart(Exon exon, int startPosition) {

		this(exon);
		this.startPosition = startPosition;
	}

	public int getStartPosition() {
		return startPosition;
	}
}
