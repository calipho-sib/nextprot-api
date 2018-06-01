package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class IsoformGeneMapping implements Serializable {

	private static final long serialVersionUID = 4L;

	private String isoformAccession;
	private String isoformMainName;
	private long referenceGeneId;
	private String referenceGeneName;

	// list of gene regions mapping this isoform protein
	private List<GeneRegion> isoformGeneRegionMappings;
	// list of transcripts mapping this isoform protein
	private List<TranscriptGeneMapping> transcriptGeneMappings;

	public IsoformGeneMapping(){
		isoformGeneRegionMappings = new ArrayList<>();
		transcriptGeneMappings = new ArrayList<>();
	}

	public String getIsoformAccession() {
		return isoformAccession;
	}

	public void setIsoformAccession(String isoformAccession) {
		this.isoformAccession = isoformAccession;
	}

	public long getReferenceGeneId() {
		return referenceGeneId;
	}

	public void setReferenceGeneId(long referenceGeneId) {
		this.referenceGeneId = referenceGeneId;
	}

	public String getReferenceGeneName() {
		return referenceGeneName;
	}

	public void setReferenceGeneName(String referenceGeneName) {
		this.referenceGeneName = referenceGeneName;
	}

    @JsonIgnore
	public List<GeneRegion> getIsoformGeneRegionMappings() {
		return isoformGeneRegionMappings;
	}

	public List<TranscriptGeneMapping> getTranscriptGeneMappings() {
		return transcriptGeneMappings;
	}

	public void setTranscriptGeneMappings(List<TranscriptGeneMapping> transcriptGeneMappings) {
		this.transcriptGeneMappings = transcriptGeneMappings;
	}

	public String getIsoformMainName() {
		return isoformMainName;
	}

	public void setIsoformMainName(String isoformMainName) {
		this.isoformMainName = isoformMainName;
	}

	@JsonIgnore
	public int getFirstPositionIsoformOnGene() {
		return isoformGeneRegionMappings.get(0).getFirstPosition();
	}

	@JsonIgnore
	public int getLastPositionIsoformOnGene() {
		return isoformGeneRegionMappings.get(isoformGeneRegionMappings.size() - 1).getLastPosition();
	}

	public String getQuality() {
	    return (transcriptGeneMappings != null && !transcriptGeneMappings.isEmpty()) ? transcriptGeneMappings.get(0).getQuality() : "BRONZE";
    }
}
