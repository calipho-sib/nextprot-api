package org.nextprot.api.core.domain.exon;

public class SimpleExonWithSequence extends SimpleExon implements Exon {

	private static final long serialVersionUID = 1L;

	private String sequence;

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
