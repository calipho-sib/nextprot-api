package org.nextprot.api.core.domain.exon;

import java.io.Serializable;

public class ExonMono extends CategorizedExon implements Serializable {

	private static final long serialVersionUID = 1L;

	private int startPosition;
	private int stopPosition;

	public ExonMono() { super(); }

	public ExonMono(Exon exon) {

		super(exon, ExonCategory.MONO);
	}

	public ExonMono(Exon exon, int startPosition, int stopPosition) {

		this(exon);
		this.startPosition = startPosition;
		this.stopPosition = stopPosition;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getStopPosition() {
		return stopPosition;
	}
}
