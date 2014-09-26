package org.nextprot.api.rdf.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.core.domain.Terminology;

@ApiObject(name = "Ontology", description = "Meta description of an ontology")
public class OWLOntology implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;	
	
	
	
	private String name;
	
	private String description;

	Terminology ontology=new Terminology();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOntology() {
		return ontology.getOntology();
	}

	public void setOntology(String ontology) {
		
		this.ontology.setOntology(ontology);
	}



}
