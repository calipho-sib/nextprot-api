package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.nextprot.api.commons.constants.TerminologyMapping;
import org.nextprot.api.commons.utils.StringUtils;

public class Terminology implements Serializable {

	private static final long serialVersionUID = 4404147147281845675L;

	private Long id;

	private String accession;

	private String name;

	private String description;

	private String ontology;

	private List<String> parentAccesion;

	private List<String> sameAs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOntology() {
		String o = StringUtils.toCamelCase(ontology, false);
		try {
			if(o != null) {
				return TerminologyMapping.valueOf(o).getDescription();
			}else return ontology;
		} catch (IllegalArgumentException e) {
			return ontology;
		}
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public List<String> getAncestorAccession() {
		return parentAccesion;
	}

	public void setAncestorAccession(String accession) {
		if (accession == null)
			return;
		List<String> all = Arrays.asList(accession.split("\\|"));
		this.parentAccesion = all;
	}

	public List<String> getSameAs() {
		return sameAs;
	}

	public void setSameAs(String sameAs) {
		if (sameAs == null)
			return;
		List<String> all = Arrays.asList(sameAs.split("\\|"));
		this.sameAs = all;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("name=");
		sb.append(name);
		sb.append("\n");
		sb.append("accession=");
		sb.append(this.accession);
		sb.append("\n");
		sb.append("ontology=");
		sb.append(this.ontology);
		sb.append("\n");
		sb.append("description=");
		sb.append(this.description);
		sb.append("\n");
		
		return sb.toString();
		
	}

}
