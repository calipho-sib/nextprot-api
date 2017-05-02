package org.nextprot.api.core.domain;

import java.io.Serializable;

public class Interactant implements Serializable {

	private static final long serialVersionUID = -628017435004233432L;

	private String url;
	private String database;
	private String accession;
	private String genename;
	private String proteinName;
	private Long xrefId;
	
	public String getProteinName() {
		return proteinName;
	}

	public void setProteinName(String proteinName) {
		this.proteinName = proteinName;
	}

	public Long getXrefId() {
		return xrefId;
	}

	public void setXrefId(Long xrefId) {
		this.xrefId = xrefId;
	}

	// if true: we have a sequence_identifiers record for this entry / isoform
	private boolean isNextprot;     
	
	// if true: the interactant is the entry or an isoform of the entry 
	// used as the :entryName in the SQL query
	private boolean isEntryPoint;   
	
	public boolean isEntryPoint() {
		return isEntryPoint;
	}

	public void setEntryPoint(boolean state) {
		this.isEntryPoint = state;
	}

	public boolean isIsoform() {
		return accession.contains("-");
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAccession() {
		return accession;
	}
	
	public String getGenename() {
		return genename;
	}
	
	public String getNextprotAccession() {
		if (isNextprot() && ! accession.startsWith("NX_")) {
			return "NX_"+accession;
		} else {
			return accession;
		}
	}
	
	

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public void setGenename(String genename) {
		this.genename = genename;
	}

	public boolean isNextprot() {
		return isNextprot;
	}

	public void setNextprot(boolean isNextprot) {
		this.isNextprot = isNextprot;
	}

	public String toString() {
		return "Interactant ac:<" + accession + "> db:" + database + " gn:" + genename + " isSrc:" + isEntryPoint + " isNX:" + isNextprot + " pn:" + proteinName; 
	}
	
}
