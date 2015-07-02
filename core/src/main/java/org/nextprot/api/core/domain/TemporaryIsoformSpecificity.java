package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.commons.utils.Pair;

/**
 * temp class for Mathieu's needs
 * should be replaced later with AnnotationIsoformSpecificity
 * 
 * @author pmichel
 *
 */
public class TemporaryIsoformSpecificity implements Serializable, Comparable<TemporaryIsoformSpecificity> {

	private static final long serialVersionUID = -6795021022182529987L;
	private String isoformName;
	private String isoformAc; 		// NX_* unique name
	private List<Pair<Integer, Integer>> positions;
	
	public TemporaryIsoformSpecificity(String isoformAc) {
		this.isoformAc = isoformAc;
	}
	
	public String getIsoformAc() {
		return isoformAc;
	}

	public void setIsoformAc(String isoformAc) {
		this.isoformAc = isoformAc;
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

	public String getSortableName() {
		if (isoformName.startsWith("Iso ")) {
			String nb = isoformName.substring(4);
			try {
				Integer.parseInt(nb);
				while (nb.length()<3) nb="0"+nb;
				return "Iso "+nb;
			} 
			catch (Exception e) {
				return isoformName;
			}
		}
		return isoformName;
	}
	
	@Override
	public int compareTo(TemporaryIsoformSpecificity o) {
		return this.getSortableName().compareTo(o.getSortableName());
	}
}
