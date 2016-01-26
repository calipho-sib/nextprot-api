package org.nextprot.api.core.domain;

import java.io.Serializable;


public class EntryProperties  implements Serializable { //TODO daniel asks: should this be a map instead???
	
	private static final long serialVersionUID = -1331815504528958900L;
	private String proteinExistence;
	private int ptmCount;
	private int varCount;
	private int isoformCount;
	private int interactionCount;

	private int maxSeqLen;
	private boolean filterstructure;
	private boolean filterdisease;
	private boolean filtermutagenesis;
	private boolean filterproteomics;
	private boolean filterexpressionprofile;
	
	private int referencesCount, referencesSubmissionsCount ,referencesAdditionalPublicationsCount, referencesCuratedPublicationsCount, referencesWebResourcesCount, referencesPatentsCount;

	public int getReferencesCount() {
		return referencesCount;
	}

	public void setReferencesCount(int referencesCount) {
		this.referencesCount = referencesCount;
	}

	public int getReferencesSubmissionsCount() {
		return referencesSubmissionsCount;
	}

	public void setReferencesSubmissionsCount(int referencesSubmissionsCount) {
		this.referencesSubmissionsCount = referencesSubmissionsCount;
	}

	public int getReferencesAdditionalPublicationsCount() {
		return referencesAdditionalPublicationsCount;
	}

	public void setReferencesAdditionalPublicationsCount(int referencesAdditionalPublicationsCount) {
		this.referencesAdditionalPublicationsCount = referencesAdditionalPublicationsCount;
	}

	public int getReferencesCuratedPublicationsCount() {
		return referencesCuratedPublicationsCount;
	}

	public void setReferencesCuratedPublicationsCount(int referencesCuratedPublicationsCount) {
		this.referencesCuratedPublicationsCount = referencesCuratedPublicationsCount;
	}

	public int getReferencesWebResourcesCount() {
		return referencesWebResourcesCount;
	}

	public void setReferencesWebResourcesCount(int referencesWebResourcesCount) {
		this.referencesWebResourcesCount = referencesWebResourcesCount;
	}

	public int getReferencesPatentsCount() {
		return referencesPatentsCount;
	}

	public void setReferencesPatentsCount(int referencesPatentsCount) {
		this.referencesPatentsCount = referencesPatentsCount;
	}

	public boolean getFilterexpressionprofile() {
		return filterexpressionprofile;
	}

	public void setFilterexpressionprofile(boolean filterexpressionprofile) {
		this.filterexpressionprofile = filterexpressionprofile;
	}

	public boolean getFilterproteomics() {
		return filterproteomics;
	}

	public void setFilterproteomics(boolean filterproteomics) {
		this.filterproteomics = filterproteomics;
	}

	public boolean getFiltermutagenesis() {
		return filtermutagenesis;
	}

	public boolean getFilterdisease() {
		return filterdisease;
	}

	public void setFilterdisease(boolean filterdisease) {
		this.filterdisease = filterdisease;
	}

	public boolean getFilterstructure() {
		return filterstructure;
	}

	public void setFilterstructure(boolean filterstructure) {
		this.filterstructure = filterstructure;
	}

	public int getInteractionCount() {
		return interactionCount;
	}

	public void setInteractionCount(int interactionCount) {
		this.interactionCount = interactionCount;
	}

	public void setFiltermutagenesis(int mutagenesisCount) {
		// a value > 0 means we have mutagenesis otherwise 0 means we don't have any
		this.filtermutagenesis = mutagenesisCount > 0? true:false;
	}

	public int getMaxSeqLen() {
		return maxSeqLen;
	}

	public void setMaxSeqLen(int maxSeqLen) {
		this.maxSeqLen = maxSeqLen;
	}

	public int getIsoformCount() {
		return isoformCount;
	}

	public void setIsoformCount(int isoformCount) {
		this.isoformCount = isoformCount;
	}

	public int getVarCount() {
		return varCount;
	}

	public void setVarCount(int varCount) {
		this.varCount = varCount;
	}


	public int getPtmCount() {
		return ptmCount;
	}

	public void setPtmCount(int ptmCount) {
		this.ptmCount = ptmCount;
	}

	public String getProteinExistence() {
		return proteinExistence;
	}

	public void setProteinExistence(String proteinExistence) {
		this.proteinExistence = proteinExistence;
	}

}
