package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;

/**
 * Property of an Annotation
 * @author dteixeira
 *
 */
public class AnnotationProperty implements Serializable {
	
	private static final long serialVersionUID = 2243514603302154352L;
	public static final String TYPE_INTERACTANT="interactant";
	public static final String NAME_RIF="resource-internal-ref";
	
	private long annotationId;
	private String type;
	private String name;
	private String value;
	private String accession;

	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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

}
