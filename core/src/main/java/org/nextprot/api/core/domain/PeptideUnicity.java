package org.nextprot.api.core.domain;

import java.util.Set;

public class PeptideUnicity {

	public enum UnicityValue {UNIQUE, PSEUDO_UNIQUE, NON_UNIQUE};
	private UnicityValue value;
	private Set<String> equivalentIsoforms;
	
	
	public static PeptideUnicity createPeptideUnicityUnique() {
		return new PeptideUnicity(UnicityValue.UNIQUE);
	}

	public static PeptideUnicity createPeptideUnicityNonUnique() {
		return new PeptideUnicity(UnicityValue.NON_UNIQUE);
	}

	public static PeptideUnicity createPeptideUnicityPseudoUnique(Set<String> equivalentIsoforms) {
		PeptideUnicity inst = new PeptideUnicity(UnicityValue.PSEUDO_UNIQUE);
		inst.equivalentIsoforms=equivalentIsoforms;
		return inst;
	}
	
	private PeptideUnicity(UnicityValue value) {
		this.value=value;
	}

	public UnicityValue getValue() {
		return this.value;
	}

	public Set<String> getEquivalentIsoforms() {
		return this.equivalentIsoforms;
	}
	
}
