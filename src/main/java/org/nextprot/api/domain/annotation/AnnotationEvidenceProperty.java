package org.nextprot.api.domain.annotation;

import java.io.Serializable;

public class AnnotationEvidenceProperty implements Serializable{

	private static final long serialVersionUID = -7303345938169180717L;
	private long evidenceId;
	private String propertyName;
	private String propertyValue;
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	public long getEvidenceId() {
		return evidenceId;
	}
	public void setEvidenceId(long evidenceId) {
		this.evidenceId = evidenceId;
	}
	
}
