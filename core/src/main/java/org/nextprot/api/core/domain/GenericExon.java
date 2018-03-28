package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.core.utils.exon.ExonCategory;

import java.io.Serializable;

public class GenericExon implements Serializable {

	private static final long serialVersionUID = 3L;

	private String name;
	private String accession;
	private String transcriptName;
	private GeneRegion geneRegion;
	private ExonCategory exonCategory;
	private int rank;
	private AminoAcid firstAminoAcid;
	private AminoAcid lastAminoAcid;

	public String getName() {
		return name;
	}

	public String getAccession() {
		return accession;
	}

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

	public GeneRegion getGeneRegion() {
		return geneRegion;
	}

	public void setGeneRegion(GeneRegion geneRegion) {
		this.geneRegion = geneRegion;
	}

	public AminoAcid getFirstAminoAcid() {
		return firstAminoAcid;
	}

	public void setFirstAminoAcid(AminoAcid firstAminoAcid) {
		this.firstAminoAcid = firstAminoAcid;
	}

	public AminoAcid getLastAminoAcid() {
		return lastAminoAcid;
	}

	public void setLastAminoAcid(AminoAcid lastAminoAcid) {
		this.lastAminoAcid = lastAminoAcid;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public ExonCategory getExonCategory() {
		return exonCategory;
	}

	public void setExonCategory(ExonCategory exonCategory) {
		this.exonCategory = exonCategory;
	}

	@JsonIgnore
	public int getFirstPositionOnGene() {
		return geneRegion.getFirstPosition();
	}

	@JsonIgnore
	public int getLastPositionOnGene() {
		return geneRegion.getLastPosition();
	}


}
