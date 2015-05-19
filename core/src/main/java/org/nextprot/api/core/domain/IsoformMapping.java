package org.nextprot.api.core.domain;

import org.nextprot.api.core.utils.NXVelocityUtils;

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

	private List<Entry<Integer,Integer>> positionsOfIsoformOnReferencedGene;
	private List<TranscriptMapping> transcriptMappings;
	
	public IsoformMapping(){
		positionsOfIsoformOnReferencedGene = new ArrayList<Entry<Integer,Integer>>();
		transcriptMappings = new ArrayList<TranscriptMapping>();
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Deprecated
	public String getIsoMainName() {
		return NXVelocityUtils.formatIsoformId(isoform);
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
