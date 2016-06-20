package org.nextprot.api.core.domain;

/**
 * Represents a normal annotation (a VP is subject + impact on a normal annotation)
 * @author Daniel Teixeira http://github.com/ddtxra
 *
 */
public class BioGenericObject extends BioObject<Isoform> {

	private static final long serialVersionUID = 1L;

	private String annotationHash;
	private String accession;
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

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
