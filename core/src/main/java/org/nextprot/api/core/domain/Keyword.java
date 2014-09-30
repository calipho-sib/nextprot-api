package org.nextprot.api.core.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "keyword", description = "A keyword")
public class Keyword implements Serializable{

	private static final long serialVersionUID = -7029978352134310155L;

	@ApiObjectField(description = "The accession code of the keyword")
	private String accession;
	@ApiObjectField(description = "The name of the keyword")
	private String name;
	
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
