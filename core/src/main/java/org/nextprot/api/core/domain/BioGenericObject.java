package org.nextprot.api.core.domain;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Objects;

/**
 * Represents a normal annotation (a VP is subject + impact on a normal annotation)
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class BioGenericObject extends BioObject {

	private static final long serialVersionUID = 2L;

	private String annotationHash;
	private String type;

	BioGenericObject() {
		super(BioType.ENTRY_ANNOTATION, ResourceType.INTERNAL, NEXTPROT_DATABASE);
	}

	public BioGenericObject(BioType bioType, ResourceType resourceType, String database) {
		super(bioType, resourceType, database);
	}

	public static BioGenericObject valueOf(AnnotationCategory annotationCategory, String database) {

		ResourceType rt = (database.equals(NEXTPROT_DATABASE)) ? ResourceType.INTERNAL : ResourceType.EXTERNAL;

		if (annotationCategory == AnnotationCategory.SMALL_MOLECULE_INTERACTION) {
			return new BioGenericObject(BioType.CHEMICAL, rt, database);
		}
		else if (annotationCategory == AnnotationCategory.BINARY_INTERACTION) {
			return new BioGenericObject(BioType.PROTEIN, rt, database);
		}

		return new BioGenericObject();
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
	protected String toBioObjectString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BioGenericObject type:").append(type).append(", annotationHash:").append(annotationHash);
		return sb.toString();
	}
}