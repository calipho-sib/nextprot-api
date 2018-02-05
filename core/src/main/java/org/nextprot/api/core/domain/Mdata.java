package org.nextprot.api.core.domain;

import java.io.Serializable;

public class Mdata implements Serializable{


	private static final long serialVersionUID = 1;

	private long evidenceId; 	// id of the parent evidence
	private long id; 			// equivalent to publication id
	private String accession;   // MDATA name      
	private String title; 
	private String rawXml; 
	
	
	public long getEvidenceId() {
		return evidenceId;
	}
	public void setEvidenceId(long evidenceId) {
		this.evidenceId = evidenceId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRawXml() {
		return rawXml;
	}
	public void setRawXml(String rawXml) {
		this.rawXml = rawXml;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id       : " + this.id + "\n");
		sb.append("ac       : " + this.accession + "\n");
		sb.append("title    : " + this.title + "'\n");
		sb.append("evi.  id : " + this.evidenceId + "\n");
		return sb.toString();
	}
	
}
