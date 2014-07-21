package org.nextprot.api.domain.export;

import org.nextprot.api.domain.exception.NextProtException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExportTemplateJsonSerializer.class)
public enum ExportXMLTemplate implements ExportTemplate{
	
	FULL("full-entry"),
	ACCESSIONS_ONLY("accessions only"),
	OVERVIEW("overview"),
	GENERAL_ANNOTATIONS("general annotations"),
	PUBLICATIONS("publications"),
	XREFS("xrefs"),
	KEYWORDS("keywords"),
	IDENTIFIERS("identifiers"),
	CHROMOSOMAL_LOCATIONS("chromosomal locations"),
	GENOMIC_MAPPINGS("genomic mappings"),
	INTERACTIONS("interactions"),
	PROTEIN_SEQUENCE("protein sequence"),
	ANTIBODY_MAPPINGS("antibody mappings"),
	PEPTIDE_MAPPINGS("pepetide mappings");
	
	
	private String templateName = null;
	private String velocityTemplate = null;

	ExportXMLTemplate(String templateName){
		this.templateName = templateName;
	}
	
	public String toString(){
		return this.templateName;
	}
	
	public static ExportXMLTemplate getTemplate (String template){
		for(ExportXMLTemplate t : values()){
			if(t.templateName.equalsIgnoreCase(template))
				return t;
		}
		throw new NextProtException(template + " export tempate not found");
	
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public String getHeader() {
		return "<entry-list>";
	}

	@Override
	public String getFooter() {
		return "</entry-list>";
	}

	@Override
	public String getVelocityTemplateName() {
		return "entry";
	}
}
