package org.nextprot.api.commons.utils;

import java.io.Serializable;

/**
 * 
 * @author pmichel
 * Represents a range of nucleotide positions on a gene / master 
 * lower bound is the first position included in the range
 * upper bound is the last position included in the range
 */
public class NucleotidePositionRange implements Serializable {
	
	private static final long serialVersionUID = -3663478691190837379L;
	
	protected final Integer lower;
	protected final Integer upper;
	
	public NucleotidePositionRange(Integer lower, Integer upper) {
		this.lower=lower; // first position included in the range
		this.upper=upper; // last position included in the range
	}
	
	public Integer getLower() {
		return lower;
	}
	
	public Integer getUpper() {
		return upper;
	}
}
