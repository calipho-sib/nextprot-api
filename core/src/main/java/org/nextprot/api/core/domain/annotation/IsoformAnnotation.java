package org.nextprot.api.core.domain.annotation;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IsoformAnnotation extends Annotation {

	private static final long serialVersionUID = -4313083647205665053L;

	private String subjectName;
	private List<String> subjectComponents;

	private Integer locationBegin; //TODO should use location or LocationRange object? Rename public method?
	private Integer locationEnd;

	private List<LocationRange> genomicLocationRanges;

	private String isoformName;

	private String annotationUniqueName;
	private String annotationHash;

	public Integer getLocationCanonicalBegin() {
		return locationBegin;
	}

	public void setLocationCanonicalBegin(Integer locationCanonicalBegin) {
		this.locationBegin = locationCanonicalBegin;
	}

	public Integer getLocationCanonicalEnd() {
		return locationEnd;
	}

	public void setLocationCanonicalEnd(Integer locationCanonicalEnd) {
		this.locationEnd = locationCanonicalEnd;
	}

	public String getIsoformName() {
		return isoformName;
	}

	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
	}

	public String getAnnotationUniqueName() {
		return annotationUniqueName;
	}

	public void setAnnotationUniqueName(String annotationUniqueName) {
		this.annotationUniqueName = annotationUniqueName;
	}

	public String getAnnotationHash() {
		return annotationHash;
	}

	public void setAnnotationHash(String annotationHash) {
		this.annotationHash = annotationHash;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public List<String> getSubjectComponents() {
		return subjectComponents;
	}

	public void setSubjectComponents(List<String> subjectComponents) {
		this.subjectComponents = subjectComponents;
	}

	public List<LocationRange> getGenomicLocationRanges() {
		return genomicLocationRanges;
	}

	public void setGenomicLocationRanges(List<LocationRange> genomicLocationRanges) {
		this.genomicLocationRanges = genomicLocationRanges;
	}
	

	@Deprecated
	public Map<String, AnnotationIsoformSpecificity> getTargetingIsoformsMap() {
		return null;
	}


}
