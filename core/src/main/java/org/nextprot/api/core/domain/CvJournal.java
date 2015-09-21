package org.nextprot.api.core.domain;

import java.io.Serializable;

public class CvJournal implements Serializable{

	private static final long serialVersionUID = -8793476383094626534L;

	private Long journalId;
	private String name;
	private String abbrev;
	private String nlmid;
	
	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}
	
	public String getNLMid() {
		return nlmid;
	}
	
	public void setNLMid(String nlmid) {
		this.nlmid = nlmid;
	}
	
	public String toString() {
		return "(id=" + journalId + ") " + name + "(short=" + abbrev + ")";
	}
	
}
