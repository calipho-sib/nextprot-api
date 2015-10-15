package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AnnotationIsoformSpecificity implements Serializable {

	private static final long serialVersionUID = 6722074138296019849L;

	// annotation isoform specificity mapping
	private static Map<String, String> specificityInfo= new HashMap<String, String>();
	
	static{
		//
		// map specificity cv_name to entity name
		specificityInfo.put("UNKNOWN", "UNKNOWN");
		specificityInfo.put("BY DEFAULT", "BY_DEFAULT");
		specificityInfo.put("SPECIFIC", "SPECIFIC");
	}

	private long annotationId; 
	// if firstPosition = null, it means that it is unknown (same as db representation)
	private Integer firstPosition; // should be at least 1
	// if lastPosition = null, it means that it is unknown (same as db representation)
	private Integer lastPosition;
	private String isoformName;
	private String specificity; // cv_name related to annotation_protein_assoc.cv_specificity_qualifier_type_id

	
	public String getSpecificity() {
		return specificityInfo.get(specificity);
	}

	public void setSpecificity(String specificity) {
		this.specificity = specificity;
	}

	public long getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(long annotationId) {
		this.annotationId = annotationId;
	}

	/** @return the first position or null if unknown */
	public Integer getFirstPosition() {
		return firstPosition;
	}

	public void setFirstPosition(Integer firstPosition) {
		this.firstPosition = firstPosition;
	}

	/** @return the first position or null if unknown */
	public Integer getLastPosition() {
		// 0 means unknown
		if (lastPosition==null) {
			return null;
		// since the firstPosition is incremented when loaded from the database, this check deals with the case when first == last
		// for annotations of type variant-insertion ...
		} else 	if(firstPosition > lastPosition) {
			return lastPosition + 1; // ... lastPosition should be the same as firstPosition in case of variant-insertion			
		} else {
			return lastPosition;
		}
	}

	public void setLastPosition(Integer lastPosition) {
		this.lastPosition = lastPosition;
	}

	// todo a helper method here?
	public boolean isPositional() {

		if (firstPosition != null)
			return true;

		if (lastPosition != null)
			return true;

		return false;

	}

	public String getIsoformName() {
		return isoformName;
	}

	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
	}

}
