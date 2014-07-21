package org.nextprot.api.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.utils.StringUtils;

@ApiObject(name = "terminology", description = "A terminology is a controlled vocabulary")
public class Terminology implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;	
	
	//
	// avoid confusing between annotation and terms that share the same name
	final static Map<String, String> termType = new HashMap<String, String>();
	public Terminology() {
		termType.put("GoMolecularFunction", "Go Molecular Function Ontology");
		termType.put("GoBiologicalProcess", "Go Biological Process Ontology");
		termType.put("GoCellularComponent", "Go Cellular Component Ontology");
		termType.put("NonStandardAminoAcid", "Non Standard Amino Acid Ontology");
		termType.put("EnzymeClassification", "Enzyme Classification Ontology");
	}
	
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
		String o=StringUtils.toCamelCase(ontology,false);		
		if (termType.containsKey(o)){
			return termType.get(o);
		}
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public List<String> getAncestorAccession() {
		return parentAccesion;
	}

	public void setAncestorAccession(String accession) {
		if(accession==null) return;
		List<String> all=Arrays.asList(accession.split("\\|"));
		this.parentAccesion = all;
	}

	public List<String> getSameAs() {
		return sameAs;
	}

	public void setSameAs(String sameAs) {
		if(sameAs==null) return;
		List<String> all=Arrays.asList(sameAs.split("\\|"));
		this.sameAs = all;
	}	

}
