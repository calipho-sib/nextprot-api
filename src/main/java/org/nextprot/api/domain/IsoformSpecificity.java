package org.nextprot.api.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nextprot.utils.Pair;

/**
 * Try to be a generic specificity for annotations, antibodies and peptides
 * @author mpereira
 *
 */
public class IsoformSpecificity implements Serializable{

	private static final long serialVersionUID = -6617265777393722080L;
	private String isoformName;
	private List<Pair<Integer, Integer>> positions;
	
	public IsoformSpecificity(String isoformName) {
		this.isoformName = isoformName;
	}
	
	public String getIsoformName() {
		return isoformName;
	}
	
	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
	}
	
	public List<Pair<Integer, Integer>> getPositions() {
		return positions;
	}
	
	public void setPositions(List<Pair<Integer, Integer>> positions) {
		this.positions = positions;
	}
	
	public void addPosition(int startPosition, int endPosition) {
		addPosition(Pair.pair(startPosition, endPosition));
	}
	
	public void addPosition(Pair<Integer, Integer> position) {
		if(this.positions == null)
			this.positions = new ArrayList<Pair<Integer,Integer>>();
		this.positions.add(position);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("specificity for isoform "+ isoformName + ": ");
		boolean afterFirst = false;
		for (Pair<Integer,Integer> pos : positions) {
			if (afterFirst) sb.append(" , ");
			sb.append(pos.getFirst() + " -> " + pos.getSecond()); 			
			afterFirst=true;
		}
		return sb.toString();
	}
}
