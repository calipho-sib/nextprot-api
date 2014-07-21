package org.nextprot.api.domain;

public class Feature {

	private String accession;
	private String isoformAccession;
	private String type;
	private String cvName;
	private Integer quality;
	private String description;
	private Integer firstPosition;
	private Integer lastPosition;
	
	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getIsoformAccession() {
		return isoformAccession;
	}
	
	public void setIsoformAccession(String isoformAccession) {
		this.isoformAccession = isoformAccession;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCvName() {
		return cvName;
	}
	
	public void setCvName(String cvName) {
		this.cvName = cvName;
	}
	
	public Integer getQuality() {
		return quality;
	}
	
	public void setQuality(Integer quality) {
		this.quality = quality;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getFirstPosition() {
		return firstPosition;
	}
	
	public void setFirstPosition(Integer firstPosition) {
		this.firstPosition = firstPosition;
	}
	
	public Integer getLastPosition() {
		return lastPosition;
	}
	
	public void setLastPosition(Integer lastPosition) {
		this.lastPosition = lastPosition;
	}
	
}
