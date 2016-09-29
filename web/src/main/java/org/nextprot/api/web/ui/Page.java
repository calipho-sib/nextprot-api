package org.nextprot.api.web.ui;

public enum Page {

	PROTEOMICS,SEQUENCE,STRUCTURES,PEPTIDES,FUNCTION;
	
	public String getUrl() {
		return name().toLowerCase();
	}
	
	public String getLabel() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();		
	}
}
