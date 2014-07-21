package org.nextprot.api.domain.export;

import org.nextprot.api.domain.exception.NextProtException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExportTemplateJsonSerializer.class)
public enum ExportTXTTemplate implements ExportTemplate{
	
	ACCESSIONS_ONLY("accessions only");
	
	private String templateName = null;
	ExportTXTTemplate(String templateName){
		this.templateName = templateName;
	}
	
	public String toString(){
		return this.templateName;
	}
	
	public static ExportTXTTemplate getTemplate (String template){
		for(ExportTXTTemplate t : values()){
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
		return "";
	}

	@Override
	public String getFooter() {
		return "";
	}

	@Override
	public String getVelocityTemplateName() {
		return "entry";
	}
}
