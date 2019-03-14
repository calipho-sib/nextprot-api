package org.nextprot.api.etl;

public enum NextProtSource {

	BioEditor("neXtProt", "http://kant.sib.swiss:9001/bioeditor"),
	GlyConnect("GlyConnect", "http://kant.sib.swiss:9001/glyconnect"),
	GnomAD("gnomAD", "http://kant.sib.swiss:9001/gnomad")
	;

	private String sourceName;
	private String statementsUrl;

	NextProtSource(String sourceName, String statementsUrl){
		this.sourceName = sourceName;
		this.statementsUrl = statementsUrl;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getStatementsUrl() {
		return statementsUrl;
	}
}
