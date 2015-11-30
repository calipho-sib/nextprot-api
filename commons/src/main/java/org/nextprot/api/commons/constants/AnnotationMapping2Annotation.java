package org.nextprot.api.commons.constants;

/**
 * Description: Used to turn peptide mapping, srm peptide mapping, antibody mapping into Annotations<br> * 
 * @author Pam
 */

public enum AnnotationMapping2Annotation  {

	PEPTIDE_MAPPING(AnnotationCategory.PEPTIDE_MAPPING.getDbAnnotationTypeName(), "GOLD", "IC", "curated", "ECO:0001096", "mass spectrometry evidence"),
	SRM_PEPTIDE_MAPPING(AnnotationCategory.SRM_PEPTIDE_MAPPING.getDbAnnotationTypeName(), "GOLD", "IC", "curated", "ECO:0001096", "mass spectrometry evidence"),
	ANTIBODY_MAPPING(AnnotationCategory.ANTIBODY_MAPPING.getDbAnnotationTypeName(), "GOLD", "IC", "curated", "ECO:0000154", "heterologous protein expression evidence")
	;

	private final String annotCat;
	private final String qualityQualifier;
	private final String assignmentMethod;
	private final String qualifierType;
	private final String ecoAC;
	private final String ecoName;
	private final String ontology = "EvidenceCodeOntologyCv";

	AnnotationMapping2Annotation(
			final String annotCat, 
			final String qualityQualifier, 
			final String assignMethod, 
			final String qualifierType, 
			String ecoAC, String ecoName) {
		
		this.annotCat=annotCat;
		this.qualityQualifier=qualityQualifier;
		this.qualifierType=qualifierType;
		this.assignmentMethod=assignMethod;
		this.ecoAC=ecoAC;
		this.ecoName=ecoName;
	}
		
	public String getEcoAC() {
		return ecoAC;
	}

	public String getEcoName() {
		return ecoName;
	}

	public String getAnnotCat() {
		return annotCat;
	}

	public String getQualityQualifier() {
		return qualityQualifier;
	}

	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public String getQualifierType() {
		return qualifierType;
	}

	public String getEcoOntology() {
		return ontology;
	}
}
