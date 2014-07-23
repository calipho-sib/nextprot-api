package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GenomicMapping implements Serializable{

	private static final long serialVersionUID = 4988428417905584804L;
	private long geneSeqId;
	private String database;
	private String accession;
	private List<IsoformMapping> isoformMappings;

	public GenomicMapping() {

		isoformMappings = new ArrayList<IsoformMapping>();
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

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public long getGeneSeqId() {
		return geneSeqId;
	}

	public void setGeneSeqId(long geneSeqId) {
		this.geneSeqId = geneSeqId;
	}

	public List<IsoformMapping> getIsoformMappings() {
		return isoformMappings;
	}

	public void setIsoformMappings(List<IsoformMapping> isoformMappings) {
		this.isoformMappings = isoformMappings;
	}

}
