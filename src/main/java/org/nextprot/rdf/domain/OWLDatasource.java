package org.nextprot.rdf.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Datasource", description = "Meta description of a source of data")
public class OWLDatasource implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;	
	


	private String name;
	
	private String description;

	private String url;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.replaceAll(" ", "_");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}



}
