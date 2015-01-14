package org.nextprot.api.core.service.export.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.exception.NextProtException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public enum NPViews{
	
	FULL_ENTRY(null, NPFileFormat.XML, NPFileFormat.TXT),
	ACCESSION("entry", NPFileFormat.XML, NPFileFormat.TXT),
	OVERVIEW("entry", NPFileFormat.XML),
	ANNOTATION("entry", NPFileFormat.XML),
	PUBLICATION("entry", NPFileFormat.XML),
	XREF("entry", NPFileFormat.XML),
	KEYWORD("entry", NPFileFormat.XML),
	IDENTIFIER("entry", NPFileFormat.XML),
	CHROMOSOMAL_LOCATION("entry", NPFileFormat.XML),
	GENOMIC_MAPPING("entry", NPFileFormat.XML),
	INTERACTION("entry", NPFileFormat.XML),
	PROTEIN_SEQUENCE("entry", NPFileFormat.XML),
	ANTIBODY("entry", NPFileFormat.XML),
	PEPTIDE("entry", NPFileFormat.XML),
	SRM_PEPTIDE_MAPPING("entry", NPFileFormat.XML);
	
	
	private String templateName = null;
	private List<NPFileFormat> supportedFormats = null;

	NPViews(String templateName, NPFileFormat ... supportedFormats){
		this.templateName = templateName;
		this.supportedFormats = Arrays.asList(supportedFormats);
	}
	
	public String getURLFormat(){
		return this.name().replaceAll("_", "-").toLowerCase();
	}
	
	public static NPViews valueOfViewName(String s){
		return NPViews.valueOf(s.toUpperCase().replaceAll("_", "-"));
	}
	
	public String toString(){
		return this.templateName;
	}
	
	public static NPViews getTemplate (String template){
		for(NPViews t : values()){
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

	private static HashMap<String, List<String>> formatViews = null;

	static {
		formatViews = new HashMap<String, List<String>>();
		for (NPFileFormat format : NPFileFormat.values()) {
			formatViews.put(format.name().toLowerCase(), new ArrayList<String>());
			for (NPViews v : NPViews.values()) {
				if (v.supportedFormats.contains(format)) {
					formatViews.get(format.name().toLowerCase()).add(v.getURLFormat());
					if(v.equals(ANNOTATION)){
						for(AnnotationApiModel a : AnnotationApiModel.values()){
							formatViews.get(format.name().toLowerCase()).add(a.getApiTypeName());
						}
					}
				}
			}
		}
	}

	public static Map<String, List<String>> getFormatViews() {
		return formatViews;
	}
}
