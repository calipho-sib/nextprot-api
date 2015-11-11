package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.util.Objects;

/**
 * Property of an Annotation
 * @author dteixeira
 *
 */
public class AnnotationProperty implements Serializable {
	
	private static final long serialVersionUID = 2243514603302154352L;
	
	private long annotationId;
	private String valueType;
	private String name;
	private String value;
	private String accession;

	
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String type) {
		this.valueType = type;
	}
	public long getAnnotationId() {
		return annotationId;
	}
	public void setAnnotationId(long annotationId) {
		this.annotationId = annotationId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AnnotationProperty)) return false;
		AnnotationProperty that = (AnnotationProperty) o;
		return annotationId == that.annotationId &&
				Objects.equals(valueType, that.valueType) &&
				Objects.equals(name, that.name) &&
				Objects.equals(value, that.value) &&
				Objects.equals(accession, that.accession);
	}

	@Override
	public int hashCode() {
		return Objects.hash(annotationId, valueType, name, value, accession);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("annot:"+ this.getAnnotationId());
		sb.append(" - name:" + this.getName());
		sb.append(" - ac:" + this.getAccession());
		sb.append(" - value:" + this.getValue());
		sb.append(" - vtype:" + this.getValueType());
		return sb.toString();
	}
}
