package org.nextprot.api.core.domain;

import java.util.Objects;

/**
 * Represents a normal annotation (a VP is subject + impact on a normal annotation)
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class BioGenericObject extends BioObject<Isoform> {

	private static final long serialVersionUID = 1L;

	private String annotationHash;
	private String type;

	public BioGenericObject() {
		// TODO for what do we need internat and nextprot?
		super(BioType.NORMAL_ANNOTATION, ResourceType.INTERNAL, NEXTPROT);
	}

	public String getAnnotationHash() {
		return annotationHash;
	}

	public void setAnnotationHash(String annotationHash) {
		this.annotationHash = annotationHash;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BioGenericObject)) return false;
		if (!super.equals(o)) return false;
		BioGenericObject that = (BioGenericObject) o;
		return Objects.equals(annotationHash, that.annotationHash) &&
				Objects.equals(type, that.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), annotationHash, type);
	}
}
