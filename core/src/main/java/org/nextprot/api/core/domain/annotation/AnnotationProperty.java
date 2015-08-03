package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;

/**
 * Property of an Annotation
 * @author dteixeira
 *
 */
public class AnnotationProperty implements Serializable {
	
	private static final long serialVersionUID = 2243514603302154352L;
	public static final String NAME_INTERACTANT="interactant";
	public static final String NAME_DIFFERING_SEQUENCE="differing sequence";	
	public static final String VALUE_TYPE_RIF="resource-internal-ref";
	public static final String VALUE_TYPE_ENTRY_AC="entry-accession";
	public static final String VALUE_TYPE_ISO_AC="isoform-accession";
	
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

}
