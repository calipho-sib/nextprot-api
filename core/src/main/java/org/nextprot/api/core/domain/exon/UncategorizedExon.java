package org.nextprot.api.core.domain.exon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GeneRegion;

public class UncategorizedExon implements Exon {

	private static final long serialVersionUID = 1L;

	private String name;
	private String accession;
	private String transcriptName;
	private GeneRegion geneRegion;
	private int rank;
	private AminoAcid firstAminoAcid;
	private AminoAcid lastAminoAcid;

	@Override
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setAccession(String accession) {
		this.accession = accession;
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
