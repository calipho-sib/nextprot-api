package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * This represents an isoform mapped to a specific gene.
 * In most cases there is only one gene, but in special cases like the HIST1H4A, Histone H4 we can have up to 14 genes (NX_P62805).
 * @author dteixeira
 *
 */
public class IsoformMapping implements Serializable{

	private static final long serialVersionUID = -7849782759942855394L;
	private long referenceGeneId;
	private String referenceGeneName;
	

	private String uniqueName;
	private Isoform isoform;

	private String bioSequence;

	// list of gene regions mapping the isoform protein
	private List<Entry<Integer,Integer>> positionsOfIsoformOnReferencedGene;
	private List<TranscriptMapping> transcriptMappings;
	
	public IsoformMapping(){
		positionsOfIsoformOnReferencedGene = new ArrayList<>();
		transcriptMappings = new ArrayList<>();
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
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

	public List<Entry<Integer, Integer>> getPositionsOfIsoformOnReferencedGene() {
		return positionsOfIsoformOnReferencedGene;
	}

	public void setPositionsOfIsoformOnReferencedGene(List<Entry<Integer, Integer>> positionsOfIsoformOnReferencedGene) {
		this.positionsOfIsoformOnReferencedGene = positionsOfIsoformOnReferencedGene;
	}


	public String getBioSequence() {
		return bioSequence;
	}

	public void setBioSequence(String bioSequence) {
		this.bioSequence = bioSequence;
	}

	public List<TranscriptMapping> getTranscriptMappings() {
		return transcriptMappings;
	}

	public void setTranscriptMappings(List<TranscriptMapping> transcriptMappings) {
		this.transcriptMappings = transcriptMappings;
	}

	public void setIsoform(Isoform isoform) {
		this.isoform = isoform;
	}

	public Isoform getIsoform() {
		return isoform;
	}

}
