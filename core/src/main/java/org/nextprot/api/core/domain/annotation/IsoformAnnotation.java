package org.nextprot.api.core.domain.annotation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IsoformAnnotation extends Annotation {

	private static final long serialVersionUID = -4313083647205665053L;

	private Integer locationBegin;
	private Integer locationEnd;
	private String isoformName;


	public String getIsoformName() {
		return isoformName;
	}

	public void setIsoformName(String isoformName) {
		this.isoformName = isoformName;
	}


	public Integer getLocationBegin() {
		return locationBegin;
	}

	public void setLocationBegin(Integer locationBegin) {
		this.locationBegin = locationBegin;
	}

	public Integer getLocationEnd() {
		return locationEnd;
	}

	public void setLocationEnd(Integer locationEnd) {
		this.locationEnd = locationEnd;
	}


	@Override
	@Deprecated
	public Map<String, AnnotationIsoformSpecificity> getTargetingIsoformsMap() {
		return null;
	}

}
