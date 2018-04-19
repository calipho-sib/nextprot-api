package org.nextprot.api.core.domain.exon;

public class ExonMono extends CategorizedExon implements FirstCodingExon, LastCodingExon {

	private static final long serialVersionUID = 1L;

	private int startPosition;
	private int stopPosition;

	public ExonMono(Exon exon) {

		super(exon, ExonCategory.MONO);
	}

	public ExonMono(Exon exon, int startPosition, int stopPosition) {

		this(exon);
		this.startPosition = startPosition;
		this.stopPosition = stopPosition;
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

    @Override
	public int getStopPosition() {
		return stopPosition;
	}
}
