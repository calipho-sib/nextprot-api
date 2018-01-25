package org.nextprot.api.core.domain;

import java.util.Set;

public class PeptideUnicity {

	public enum Value {UNIQUE, PSEUDO_UNIQUE, NOT_UNIQUE};
	private Value value;
	private Set<String> equivalentIsoforms;
	
	
	public static PeptideUnicity createPeptideUnicityUnique() {
		return new PeptideUnicity(Value.UNIQUE);
	}

	/**
	 * Creates a non unique PeptideUnicity
	 * 
	 * @param equivalentIsoforms optional set of equivalent isoforms if among the matching isoforms some are equivalent (same sequence)
	 * @return a new PeptideUnicity
	 */
	public static PeptideUnicity createPeptideUnicityNonUnique(Set<String> equivalentIsoforms) {
		PeptideUnicity inst = new PeptideUnicity(Value.NOT_UNIQUE);
		if (equivalentIsoforms !=null && equivalentIsoforms.size()>0) inst.equivalentIsoforms=equivalentIsoforms;
		return inst;
	}

	/**
	 * Creates a non unique PeptideUnicity
	 * 
	 * @param equivalentIsoforms the set of equivalent isoforms (same sequence) among the matching isoforms, the set should not be null nor empty
	 * @return a new PeptideUnicity
	 */
	public static PeptideUnicity createPeptideUnicityPseudoUnique(Set<String> equivalentIsoforms) {
		PeptideUnicity inst = new PeptideUnicity(Value.PSEUDO_UNIQUE);
		inst.equivalentIsoforms=equivalentIsoforms;
		return inst;
	}
	
	private PeptideUnicity(Value value) {
		this.value=value;
	}

	public Value getValue() {
		return this.value;
	}

	public Set<String> getEquivalentIsoforms() {
		return this.equivalentIsoforms;
	}
	
}
