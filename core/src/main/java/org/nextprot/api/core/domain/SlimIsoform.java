package org.nextprot.api.core.domain;

import java.io.Serializable;


public class SlimIsoform implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accession;
	private String md5;
	private String sequence;

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
