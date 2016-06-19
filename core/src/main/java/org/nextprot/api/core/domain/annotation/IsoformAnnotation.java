package org.nextprot.api.core.domain.annotation;

import java.util.List;
import java.util.Map;

public class IsoformAnnotation extends Annotation{
	
	private static final long serialVersionUID = -4313083647205665053L;

	private String subjectName;
	private List<String> subjectComponents;
	
	private Integer locationCanonicalBegin;
	private Integer locationGenomicBegin;

	private Integer locationCanonicalEnd;
	private Integer locationGenomicEnd;
	
	private String isoformName;
	
	private String annotationUniqueName;
	private String annotationHash;
	
	
	@Deprecated
	public Map<String, AnnotationIsoformSpecificity> getTargetingIsoformsMap() {
		return super.getTargetingIsoformsMap();
	}

	public Integer getLocationCanonicalBegin() {
		return locationCanonicalBegin;
	}

	public void setLocationCanonicalBegin(Integer locationCanonicalBegin) {
		this.locationCanonicalBegin = locationCanonicalBegin;
	}

	public Integer getLocationGenomicBegin() {
		return locationGenomicBegin;
	}

	public void setLocationGenomicBegin(Integer locationGenomicBegin) {
		this.locationGenomicBegin = locationGenomicBegin;
	}

	public Integer getLocationCanonicalEnd() {
		return locationCanonicalEnd;
	}

	public void setLocationCanonicalEnd(Integer locationCanonicalEnd) {
		this.locationCanonicalEnd = locationCanonicalEnd;
	}

	public Integer getLocationGenomicEnd() {
		return locationGenomicEnd;
	}

	public void setLocationGenomicEnd(Integer locationGenomicEnd) {
		this.locationGenomicEnd = locationGenomicEnd;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
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

}
