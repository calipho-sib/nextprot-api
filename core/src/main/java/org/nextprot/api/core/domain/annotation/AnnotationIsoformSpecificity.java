package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationIsoformSpecificity implements Serializable, Comparable<AnnotationIsoformSpecificity> {

	private static final long serialVersionUID = 6722074138296019849L;

	private static final DecimalFormat ISO_NUMBER_FORMATTER = new DecimalFormat("000");
	private static final Pattern ISO_PATTERN = Pattern.compile("Iso (\\d+)");

	// annotation isoform specificity mapping
	@Deprecated //should use the enum IsoTargetSpecificity
	private static Map<String, String> specificityInfo= new HashMap<>();
	
	static{ //TODO this should be removed! Should use 
		//
		// map specificity cv_name to entity name
		specificityInfo.put("UNKNOWN", "UNKNOWN");
		specificityInfo.put("BY DEFAULT", "BY_DEFAULT");
		specificityInfo.put("BY_DEFAULT", "BY_DEFAULT");
		specificityInfo.put("SPECIFIC", "SPECIFIC");
	}

	private long annotationId; 
	// if firstPosition = null, it means that it is unknown (same as db representation)
	private Integer firstPosition; // should be at least 1
	// if lastPosition = null, it means that it is unknown (same as db representation)
	private Integer lastPosition;
	private String isoformAccession;
	private String specificity; // cv_name related to annotation_protein_assoc.cv_specificity_qualifier_type_id

	private String name;

	private String hgvs;
	
	private String _comparableName;
	
	public void setHgvs(String hgvs) {
		this.hgvs = hgvs;
	}
	
	public String getHgvs() {
		return this.hgvs;
	}
	
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
		} else 	if(firstPosition !=null && firstPosition > lastPosition) {
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

	
	public String getIsoformAccession() {
		return isoformAccession;
	}

	public void setIsoformAccession(String ac) {
		this.isoformAccession = (ac != null) ? ac : "";
		_comparableName = (this.isoformAccession.startsWith("Iso ")) ? formatIsoName(this.isoformAccession) : this.isoformAccession;
	}

	@Deprecated
	public String getIsoformName() {
		return isoformAccession;
	}

	//TODO looks like we always set the accession here. Should be setIsoformAccession instead
	// => yes, always used to set ac, never used to set name, so set as deprecated (pam, 11.2016)
	@Deprecated
	public void setIsoformName(String isoformName) {
		this.isoformAccession = (isoformName != null) ? isoformName : "";
		_comparableName = (this.isoformAccession.startsWith("Iso ")) ? formatIsoName(this.isoformAccession) : this.isoformAccession;
	}

	static String formatIsoName(String name) {

		Matcher matcher = ISO_PATTERN.matcher(name);

		if (matcher.find()) {

			return "Iso "+ISO_NUMBER_FORMATTER.format(Integer.parseInt(matcher.group(1)));
		}

		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AnnotationIsoformSpecificity)) return false;
		AnnotationIsoformSpecificity that = (AnnotationIsoformSpecificity) o;
		return annotationId == that.annotationId &&
				Objects.equals(firstPosition, that.firstPosition) &&
				Objects.equals(lastPosition, that.lastPosition) &&
				Objects.equals(isoformAccession, that.isoformAccession) &&
				Objects.equals(specificity, that.specificity) &&
				Objects.equals(_comparableName, that._comparableName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(annotationId, firstPosition, lastPosition, isoformAccession, specificity, _comparableName);
	}

	@Override
	public int compareTo(AnnotationIsoformSpecificity other) {

		return _comparableName.compareTo(other._comparableName);
	}

	public boolean hasSameIsoformPositions(AnnotationIsoformSpecificity other) {

		return isoformAccession.equals(other.isoformAccession) &&
				Objects.equals(firstPosition, other.firstPosition) && Objects.equals(lastPosition, other.lastPosition);
	}

	/**
	 * This is NOT for the isoform name, it is used to set the variant name for example.
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
