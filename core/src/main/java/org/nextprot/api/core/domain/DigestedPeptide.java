package org.nextprot.api.core.domain;

import java.io.Serializable;

public class DigestedPeptide implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sequence;
	private int producedWithMiscleavageCount;
	
	public DigestedPeptide() {	}
	
	public DigestedPeptide(String sequence, int producedWithMiscleavageCount) {
		this.producedWithMiscleavageCount=producedWithMiscleavageCount;
		this.sequence=sequence;
	}
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public int getProducedWithMiscleavageCount() {
		return producedWithMiscleavageCount;
	}
	public void setProducedWithMiscleavageCount(int producedWithMiscleavageCount) {
		this.producedWithMiscleavageCount = producedWithMiscleavageCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DigestedPeptide other = (DigestedPeptide) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}
	
	
	
	
}
