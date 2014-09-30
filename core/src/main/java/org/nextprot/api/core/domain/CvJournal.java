package org.nextprot.api.core.domain;

import java.io.Serializable;

public class CvJournal implements Serializable{

	private static final long serialVersionUID = -8793476383094626534L;

	private Long journalId;
	private String name;
	
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
	
}
