package org.nextprot.api.web.ui;

public enum EntryPage {

	FUNCTION("Function", ""),
	MEDICAL("Medical"),
	EXPRESSION("Expression"),
	INTERACTIONS("Interactions"),
	LOCALISATION("Localisation"),
	SEQUENCE("Sequence"),
	PROTEOMICS("Proteomics"),
	STRUCTURES("Structures"),
	PROTEIN_IDENTIFIERS("Identifiers"),
	PEPTIDES("Peptides"),
	PHENOTYPES("Phenotypes"),
	EXONS("Exons"),
	GENE_IDENTIFIERS("Identifiers", "gene_identifiers")
	;

	private final String link;
	private final String label;

	EntryPage(String label) {

		this.label = label;
		this.link = label.toLowerCase();
	}

	EntryPage(String label, String link) {

		this.label = label;
		this.link = link;
	}

	public String getLabel() {
		return label;
	}

	public String getLink() {
		return link;
	}
}
