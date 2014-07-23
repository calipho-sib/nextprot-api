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
	private int firstPosition = 0; // should be at least 1
	private int lastPosition = 0;
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

	public int getFirstPosition() {
		return firstPosition;
	}

	public void setFirstPosition(int firstPosition) {
		this.firstPosition = firstPosition;
	}

	public int getLastPosition() {
		if(firstPosition > lastPosition) //since the firstPosition is incremented when loaded from the database, this check deals with the case when first == last. For annotations of type variant-insertion
			return lastPosition + 1; // should be the same as firstPosition
		else return lastPosition;
	}

	public void setLastPosition(int lastPosition) {
		this.lastPosition = lastPosition;
	}

	// todo a helper method here?
	public boolean isPositional() {

		if (firstPosition != 0)
			return true;

		if (lastPosition != 0)
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
