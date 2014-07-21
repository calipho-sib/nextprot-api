package org.nextprot.api.domain;

import java.io.Serializable;

public class Family implements Serializable{

	private static final long serialVersionUID = -2044466405961942191L;
	private String accession;
	private String name;
	private String description;
	private String region;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAccession() {
		return accession;
	}
	
	public void setAccession(String accession) {
		this.accession = accession;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
