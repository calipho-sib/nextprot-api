package org.nextprot.api.rdf.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.commons.constants.TerminologyMapping;
import org.nextprot.api.commons.utils.StringUtils;

@ApiObject(name = "Ontology", description = "Meta description of an ontology")
public class OWLOntology implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;	
	
	private String name;
	
	private String description;

	private String ontology;
	
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
		return ontology;
	}

	public void setOntology(String ontology) {
		String o = StringUtils.toCamelCase(ontology, false);
		try {
			this.ontology = TerminologyMapping.valueOf(o).getDescription();
		}catch (IllegalArgumentException e) {
			this.ontology = ontology;
		}
	}



}
