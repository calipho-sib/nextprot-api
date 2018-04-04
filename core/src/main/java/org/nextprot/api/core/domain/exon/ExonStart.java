package org.nextprot.api.core.domain.exon;

public class ExonStart extends CategorizedExon {

	private static final long serialVersionUID = 1L;

	private int startPosition;

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
