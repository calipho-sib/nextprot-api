package org.nextprot.api.core.domain.exon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

import java.io.Serializable;

public class CategorizedExon implements Exon, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String accession;
	private String transcriptName;
	private GeneRegion geneRegion;
	private ExonCategory exonCategory;
	private int rank;
	private AminoAcid firstAminoAcid;
	private AminoAcid lastAminoAcid;

	public CategorizedExon(ExonCategory exonCategory) {
		this.exonCategory = exonCategory;
	}

	public void fillFrom(UncategorizedExon exon) {

		this.name = exon.getName();
		this.accession = exon.getAccession();
		this.transcriptName = exon.getTranscriptName();
		this.geneRegion = exon.getGeneRegion();
		this.rank = exon.getRank();
		this.firstAminoAcid= exon.getFirstAminoAcid();
		this.lastAminoAcid= exon.getLastAminoAcid();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public String getTranscriptName() {
		return transcriptName;
	}

	@Override
	public GeneRegion getGeneRegion() {
		return geneRegion;
	}

	@Override
	public AminoAcid getFirstAminoAcid() {
		return firstAminoAcid;
	}

	@Override
	public AminoAcid getLastAminoAcid() {
		return lastAminoAcid;
	}

	@Override
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public ExonCategory getExonCategory() {
		return exonCategory;
	}

	@JsonIgnore
	@Override
	public int getFirstPositionOnGene() {
		return geneRegion.getFirstPosition();
	}

	@JsonIgnore
	@Override
	public int getLastPositionOnGene() {
		return geneRegion.getLastPosition();
	}
}
