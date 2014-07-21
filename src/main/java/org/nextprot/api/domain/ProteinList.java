package org.nextprot.api.domain;

import java.util.HashSet;
import java.util.Set;

public class ProteinList {

	private Long id;
	private String name;
	private String description;
	private Set<String> accessions = new HashSet<String>();
	private int accSize = 0;
	private Long ownerId;
	
	public ProteinList() { }
	
	public ProteinList(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getAccessions() {
		return accessions;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAccessions(Set<String> accessions) {
		this.accessions = accessions;
		this.accSize = this.accessions.size();
	}
	
	public int getAccSize() {
		return accSize;
	}

	public void setAccSize(int accSize) {
		this.accSize = accSize;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	public String toString() {
		return "name: "+this.name+" description: "+this.description+" accessions: "+this.accessions.size()+"owner: "+this.ownerId;
	}
	
}
