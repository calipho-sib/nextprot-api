package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.List;


public class TranscriptMapping implements Serializable{

	private static final long serialVersionUID = -9783197460101612L;
	private long referenceGeneId;
	private String referenceGeneUniqueName;
	
	private String isoformName;
	private String database;
	private String accession;
	private String uniqueName;
	private String proteinId;
	private String bioSequence;
	private String quality;
	
	private List<Exon> exons;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getProteinId() {
		return proteinId;
	}

	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	/**
	 * Gets the exons in ascending order (first position comes first in the list)
	 */
	public List<Exon> getExons() {
		return exons;
	}

	/**
	 * Sets the exons. 
	 * When setting the exons be careful, to set them in ascending order (first positions comes first in the list)
	 * @param exons
	 */
	public void setExons(List<Exon> exons) {
		this.exons = exons;
	}

	
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}


	public String getBioSequence() {
		return bioSequence;
	}

	public void setBioSequence(String bioSequence) {
		this.bioSequence = bioSequence;
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
