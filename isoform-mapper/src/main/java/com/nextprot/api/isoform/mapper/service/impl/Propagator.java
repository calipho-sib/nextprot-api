package com.nextprot.api.isoform.mapper.service.impl;

import org.nextprot.api.core.dao.EntityName;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

public class Propagator {

	private Entry entry;
	
	public Propagator(Entry entry) {
		this.entry=entry;
	}
	
	public Entry getEntry() {
		return this.getEntry();
	}
	
	public CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, Isoform isoform) {
		return PropagatorCore.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, String isoformName) {
		Isoform isoform = getIsoformByName(isoformName);
		return PropagatorCore.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, Isoform isoform) {
		return PropagatorCore.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, String isoformName) {
		Isoform isoform = getIsoformByName(isoformName);
		return PropagatorCore.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public Integer getProjectedPosition(Isoform srcIsoform, int srcPosition, Isoform trgIoform) {
		CodonNucleotidePositions cnPositions = PropagatorCore.getCodonNucleotidesPositionsInRanges(srcPosition, srcIsoform.getMasterMapping());
		CodonNucleotideIndices cnIndices = PropagatorCore.getCodonNucleotidesIndicesInRanges(cnPositions, trgIoform.getMasterMapping());
		return cnIndices.getAminoAcidPosition();
	}
	

	/**
	 * Return an isoform object having unique name, main name or synonym equals to name 	
	 * @param name an isocform unique name (ac), main name or synonym
	 * @return
	 */
	public Isoform getIsoformByName(String name) {
		if (name==null) return null;
		for (Isoform iso: entry.getIsoforms()) {
			if (name.equals(iso.getUniqueName())) return iso;
			EntityName mainEname = iso.getMainEntityName();
			if (mainEname!=null && name.equals(mainEname.getName())) return iso; 
			for (EntityName syn: iso.getSynonyms()) {
				if (name.equals(syn.getName())) return iso;
			}
		}
		return null;		
	}
	
	/**
	 * Return the canonical isoform of the entry
	 * @return
	 */
	public  Isoform getCanonicalIsoform() {
		for (Isoform iso: entry.getIsoforms()) {
			if (iso.isCanonicalIsoform()) return iso;
		}
		return null;
	}
	
	/**
	 * Check that we have amino acid aa(s) in isoform sequence at position pos.
	 * If aa iss null or empty string we just check that position is < sequence lenght
	 * @param isoform
	 * @param pos position according to bio standard (first pos = 1)
	 * @param aa 0, 1 or more amino acids (1 char / aa)
	 * @return
	 */
	public  boolean checkAminoAcidPosition(Isoform isoform, int pos, String aa) {
		return PropagatorCore.checkAminoAcidPosition(isoform.getSequence(), pos, aa);
	}
	

}
