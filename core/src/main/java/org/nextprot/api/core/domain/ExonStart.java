package org.nextprot.api.core.domain;

import org.nextprot.api.core.utils.exon.ExonCategory;

import java.io.Serializable;

public class ExonStart implements Exon, Serializable {

	private static final long serialVersionUID = 1L;

	private Exon exon;
	private int startPosition;

	public ExonStart() { }

	public ExonStart(Exon exon, int startPosition) {

		if (exon.getExonCategory() != ExonCategory.START) {
			throw new IllegalArgumentException("cannot make an exon start from "+exon.getExonCategory());
		}
		this.exon = exon;
		this.startPosition= startPosition;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	@Override
	public String getName() {
		return exon.getName();
	}

	@Override
	public String getAccession() {
		return exon.getAccession();
	}

	@Override
	public String getTranscriptName() {
		return exon.getTranscriptName();
	}

	@Override
	public GeneRegion getGeneRegion() {
		return exon.getGeneRegion();
	}

	@Override
	public AminoAcid getFirstAminoAcid() {
		return exon.getFirstAminoAcid();
	}

	@Override
	public AminoAcid getLastAminoAcid() {
		return exon.getLastAminoAcid();
	}

	@Override
	public int getRank() {
		return exon.getRank();
	}

	@Override
	public ExonCategory getExonCategory() {
		return exon.getExonCategory();
	}

	@Override
	public int getFirstPositionOnGene() {
		return exon.getFirstPositionOnGene();
	}

	@Override
	public int getLastPositionOnGene() {
		return exon.getLastPositionOnGene();
	}
}
