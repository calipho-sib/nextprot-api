package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;


public class EntryProperties  implements Serializable { //TODO daniel asks: should this be a map instead???
	
	private static final long serialVersionUID = 4L;

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

	private Map<ProteinExistence.Source, ProteinExistence> proteinExistence = new EnumMap<>(ProteinExistence.Source.class);

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

	@JsonIgnore
	public Map<ProteinExistence.Source, ProteinExistence> getProteinExistenceMap() {

		return proteinExistence;
	}

	public ProteinExistence getProteinExistence(ProteinExistence.Source source) {

		return proteinExistence.get(source);
	}

	public ProteinExistence getProteinExistence() {

		return proteinExistence.get(ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT2);
	}

	public void addProteinExistenceForSource(ProteinExistence.Source source, ProteinExistence pe) {

		proteinExistence.put(source, pe);
	}
}
