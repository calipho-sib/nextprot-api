package org.nextprot.api.core.domain.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;

public class CategorizedExon implements Exon, Serializable {

	private static final long serialVersionUID = 1L;

	private Exon exon;
	private ExonCategory exonCategory;

	public CategorizedExon() {}

	public CategorizedExon(Exon exon, ExonCategory exonCategory) {
		this.exon= exon;
		this.exonCategory = exonCategory;
	}

	public ExonCategory getExonCategory() {
		return exonCategory;
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
	public int getFirstPositionOnGene() {
		return exon.getFirstPositionOnGene();
	}

	@Override
	public int getLastPositionOnGene() {
		return exon.getLastPositionOnGene();
	}
}
