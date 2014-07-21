package org.nextprot.api.domain.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "genomic-mapping", description = "The genomic mapping")
public class GenomicMapping implements Serializable{

	private static final long serialVersionUID = 4988428417905584804L;
	private long geneSeqId;

	@ApiObjectField(description = "The database field")
	private String database;

	@ApiObjectField(description = "The accession")
	private String accession;

	@ApiObjectField(description = "The isoform mappings")
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
