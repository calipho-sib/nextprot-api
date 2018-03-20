package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents an isoform mapped to a specific gene.
 * In most cases there is only one gene, but in special cases like the HIST1H4A, Histone H4 we can have up to 14 genes (NX_P62805).
 * @author dteixeira
 *
 */
public class IsoformGeneMapping implements Serializable {

	private static final long serialVersionUID = 2L;

	private long referenceGeneId;
	private String referenceGeneName;
	private String isoformName;
	private Isoform isoform;

	// list of gene regions mapping this isoform protein
	private List<GeneRegion> isoformCodingGeneRegionMappings;
	// list of transcripts mapping this isoform protein
	private List<TranscriptGeneMapping> transcriptGeneMappings;
	
	public IsoformGeneMapping(){
		isoformCodingGeneRegionMappings = new ArrayList<>();
		transcriptGeneMappings = new ArrayList<>();
	}

	public String getIsoformName() {
		return isoformName;
	}

	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
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

	public List<GeneRegion> getIsoformCodingGeneRegionMappings() {
		return isoformCodingGeneRegionMappings;
	}

	/** @deprecated  As of serialVersionUID 2L, replaced by {@link #getIsoform().getSequence()} */
	@Deprecated
	public String getBioSequence() {

		return getAminoAcidSequence();
	}

    /** @deprecated  As of serialVersionUID 2L, replaced by {@link #getIsoform().getSequence()}} */
    @Deprecated
	public String getAminoAcidSequence() {
		return (isoform != null) ? isoform.getSequence() : "";
	}

	public List<TranscriptGeneMapping> getTranscriptGeneMappings() {
		return transcriptGeneMappings;
	}

	public void setTranscriptGeneMappings(List<TranscriptGeneMapping> transcriptGeneMappings) {
		this.transcriptGeneMappings = transcriptGeneMappings;
	}

	public void setIsoform(Isoform isoform) {
		this.isoform = isoform;
	}

	public Isoform getIsoform() {
		return isoform;
	}

	@JsonIgnore
	public int getFirstPositionIsoformOnGene() {
		return isoformCodingGeneRegionMappings.get(0).getFirstPosition();
	}

	@JsonIgnore
	public int getLastPositionIsoformOnGene() {
		return isoformCodingGeneRegionMappings.get(isoformCodingGeneRegionMappings.size() - 1).getLastPosition();
	}
}
