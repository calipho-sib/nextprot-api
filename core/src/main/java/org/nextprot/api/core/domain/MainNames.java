package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.List;

public class MainNames implements Serializable{

	private static final long serialVersionUID = 7256373617827647383L;

	private String accession;
	private String name;
	private List<String> geneNameList;
	private String url;
	
	public String getAccession() {
		return accession;
	}
	public String getEntryAccession() {
		if (accession.contains("-")) {
			return accession.substring(0,accession.indexOf("-"));
		} else {
			return accession;
		}
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
	public List<String> getGeneNameList() {
		return geneNameList;
	}
	public void setGeneNameList(List<String> geneNameList) {
		this.geneNameList = geneNameList;
	}
	public String getUrl() {
		return "https://www.nextprot.org/entry/" + accession;			
		/*
		if (accession.contains("-")) {
			return null;
		} else {
			return "https://www.nextprot.org/entry/" + accession;			
		}
		*/
	}

}
