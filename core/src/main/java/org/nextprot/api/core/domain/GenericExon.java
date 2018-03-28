package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.core.utils.exon.ExonCategory;

import java.io.Serializable;

public class GenericExon implements Exon, Serializable {

	private static final long serialVersionUID = 3L;

	private String name;
	private String accession;
	private String transcriptName;
	private GeneRegion geneRegion;
	private ExonCategory exonCategory;
	private int rank;
	private AminoAcid firstAminoAcid;
	private AminoAcid lastAminoAcid;

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

	public void setTranscriptName(String transcriptName) {
		this.transcriptName = transcriptName;
	}

	public void setNameDeduceAccession(String exonName) {
		this.name = exonName;
		this.accession = exonName.substring(3);
	}

	@Override
	public GeneRegion getGeneRegion() {
		return geneRegion;
	}

	public void setGeneRegion(GeneRegion geneRegion) {
		this.geneRegion = geneRegion;
	}

	@Override
	public AminoAcid getFirstAminoAcid() {
		return firstAminoAcid;
	}

	public void setFirstAminoAcid(AminoAcid firstAminoAcid) {
		this.firstAminoAcid = firstAminoAcid;
	}

	@Override
	public AminoAcid getLastAminoAcid() {
		return lastAminoAcid;
	}

	public void setLastAminoAcid(AminoAcid lastAminoAcid) {
		this.lastAminoAcid = lastAminoAcid;
	}

	@Override
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public ExonCategory getExonCategory() {
		return exonCategory;
	}

	public void setExonCategory(ExonCategory exonCategory) {
		this.exonCategory = exonCategory;
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
