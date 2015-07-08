package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.utils.Pair;

/**
 * Try to be a generic specificity for annotations, antibodies and peptides
 * @author mpereira
 *
 */
public class IsoformSpecificity implements Serializable, Comparable<IsoformSpecificity>{

	private static final long serialVersionUID = -6617265777393722080L;

	@Deprecated
	private String deprecatedIsoformName;
	private String isoformMainName;
	private String isoformAc;
	
	public void setIsoformAc(String isoformAc) {
		this.isoformAc = isoformAc;
	}


	private List<Pair<Integer, Integer>> positions;

	public IsoformSpecificity(String isoformName, String isoformAc) {
		this.isoformMainName = isoformName;
		this.isoformAc = isoformAc;
	}

	//isoform name should be replaced with ac
	@Deprecated
	public IsoformSpecificity(String isoformName) {
		this.deprecatedIsoformName = isoformName;
	}

	public String setIsoformMainName(String isoformMainName) {
		return this.isoformMainName = isoformMainName;
	}
	
	public String getIsoformMainName() {
		return isoformMainName;
	}

	public String getIsoformAc() {
		return isoformAc;
	}

	@Deprecated
	public String getIsoformName() {
		return deprecatedIsoformName;
	}

	@Deprecated
	public void setIsoformName(String isoformName) {
		this.deprecatedIsoformName = isoformName;
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
		sb.append("specificity for isoform "+ deprecatedIsoformName + ": ");
		boolean afterFirst = false;
		for (Pair<Integer,Integer> pos : positions) {
			if (afterFirst) sb.append(" , ");
			sb.append(pos.getFirst() + " -> " + pos.getSecond()); 			
			afterFirst=true;
		}
		return sb.toString();
	}
	
	
	public String getSortableName() {
		if (isoformMainName.startsWith("Iso ")) {
			String nb = isoformMainName.substring(4);
			try {
				Integer.parseInt(nb);
				while (nb.length()<3) nb="0"+nb;
				return "Iso "+nb;
			} 
			catch (Exception e) {
				return isoformMainName;
			}
		}
		return isoformMainName;
	}
	
	
	@Override
	public int compareTo(IsoformSpecificity o) {
		return this.getSortableName().compareTo(o.getSortableName());
	}


}
