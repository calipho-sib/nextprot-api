package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.List;


public class TranscriptGeneMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private long referenceGeneId;
	private String referenceGeneUniqueName;
	private String isoformName;
	private String database;
	private String databaseAccession;
	private String uniqueName;
	private String proteinId;
	private int nucleotideSequenceLength;
	private String quality;
	private List<GenericExon> exons;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDatabaseAccession() {
		return databaseAccession;
	}

	public void setDatabaseAccession(String databaseAccession) {
		this.databaseAccession = databaseAccession;
	}

	public String getProteinId() {
		return proteinId;
	}

	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}

	public String getName() {
		return uniqueName;
	}

	public void setName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	/**
	 * Gets the exons in ascending order (first position comes first in the list)
	 */
	public List<GenericExon> getExons() {
		return exons;
	}

	/**
	 * Sets the exons. 
	 * When setting the exons be careful, to set them in ascending order (first positions comes first in the list)
	 * @param exons
	 */
	public void setExons(List<GenericExon> exons) {
		this.exons = exons;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public int getNucleotideSequenceLength() {
		return nucleotideSequenceLength;
	}

	public void setNucleotideSequenceLength(int nucleotideSequenceLength) {
		this.nucleotideSequenceLength = nucleotideSequenceLength;
	}

	public String getIsoformName() {
		return isoformName;
	}

	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
	}

	public long getReferenceGeneId() {
		return referenceGeneId;
	}

	public void setReferenceGeneId(long referenceGeneId) {
		this.referenceGeneId = referenceGeneId;
	}

	public String getReferenceGeneUniqueName() {
		return referenceGeneUniqueName;
	}

	public void setReferenceGeneUniqueName(String referenceGeneUniqueName) {
		this.referenceGeneUniqueName = referenceGeneUniqueName;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

}
