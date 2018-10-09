package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.Set;

public class SequenceUnicity implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Value {UNIQUE, PSEUDO_UNIQUE, NOT_UNIQUE};
	private Value value;
	private Set<String> equivalentIsoforms;
	
	
	public static SequenceUnicity createSequenceUnicityUnique() {
		return new SequenceUnicity(Value.UNIQUE);
	}

	/**
	 * Creates a non unique SequenceUnicity
	 * 
	 * @param equivalentIsoforms optional set of equivalent isoforms if among the matching isoforms some are equivalent (same sequence)
	 * @return a new SequenceUnicity
	 */
	public static SequenceUnicity createSequenceUnicityNonUnique(Set<String> equivalentIsoforms) {
		SequenceUnicity inst = new SequenceUnicity(Value.NOT_UNIQUE);
		if (equivalentIsoforms !=null && equivalentIsoforms.size()>0) inst.equivalentIsoforms=equivalentIsoforms;
		return inst;
	}

	/**
	 * Creates a pseudo unique SequenceUnicity
	 * 
	 * @param equivalentIsoforms the set of equivalent isoforms (same sequence) among the matching isoforms, the set should not be null nor empty
	 * @return a new SequenceUnicity
	 */
	public static SequenceUnicity createSequenceUnicityPseudoUnique(Set<String> equivalentIsoforms) {
		SequenceUnicity inst = new SequenceUnicity(Value.PSEUDO_UNIQUE);
		inst.equivalentIsoforms=equivalentIsoforms;
		return inst;
	}
	
	private SequenceUnicity(Value value) {
		this.value=value;
	}

	public Value getValue() {
		return this.value;
	}

	public Set<String> getEquivalentIsoforms() {
		return this.equivalentIsoforms;
	}
	
}
