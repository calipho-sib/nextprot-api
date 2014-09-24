package org.nextprot.api.core.domain;

import java.io.Serializable;

public class Exon implements Serializable{

	private static final long serialVersionUID = -2078534367601549856L;
	private int rank;
	private String codingStatus;
	private String accession;
	private int firstPositionOnGene;
	private int lastPositionOnGene;
	private String transcriptName;
	private String geneName;
	
	private String name;
	private String bioSequence;

	private AminoAcid firstAminoAcid;
	private AminoAcid lastAminoAcid;
	
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getCodingStatus() {
		return codingStatus;
	}

	public void setCodingStatus(String codingStatus) {
		this.codingStatus = codingStatus;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
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

	public String getTranscriptName() {
		return transcriptName;
	}

	public void setTranscriptName(String transcriptName) {
		this.transcriptName = transcriptName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.accession = name.substring(3);
	}

	public int getLastPositionOnGene() {
		return lastPositionOnGene;
	}

	public void setLastPositionOnGene(int lastPositionOnGene) {
		this.lastPositionOnGene = lastPositionOnGene;
	}

	public int getFirstPositionOnGene() {
		return firstPositionOnGene;
	}

	public void setFirstPositionOnGene(int firstPositionOnGene) {
		this.firstPositionOnGene = firstPositionOnGene;
	}

	public String getGeneName() {
		return geneName;
	}

	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}



}
