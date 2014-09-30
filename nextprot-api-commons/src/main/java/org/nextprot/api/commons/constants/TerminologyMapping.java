package org.nextprot.api.commons.constants;

public enum TerminologyMapping {

	GoMolecularFunction("Go Molecular Function Ontology"), 
	GoBiologicalProcess("Go Biological Process Ontology"), 
	GoCellularComponent("Go Cellular Component Ontology"), 
	NonStandardAminoAcid("Non Standard Amino Acid Ontology"), 
	EnzymeClassification("Enzyme Classification Ontology");

	String description;

	TerminologyMapping(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
