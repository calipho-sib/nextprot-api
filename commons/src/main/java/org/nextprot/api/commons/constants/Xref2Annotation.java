package org.nextprot.api.commons.constants;

/**
 * Description: Used to turn some XRefs into Annotations<br> * 
 * @author Pam
 */

public enum Xref2Annotation  {

	ORPHANET("Orphanet", "disease", AnnotationCategory.DISEASE.getDbAnnotationTypeName(),
			"Orphanet","GOLD","curated", "IC","ECO:0000305", "curator inference used in manual assertion"),
	REACTOME("Reactome", "pathway name", AnnotationCategory.PATHWAY.getDbAnnotationTypeName(), 
			"Reactome","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion"),
	DRUGBANK("DrugBank", "generic name", AnnotationCategory.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName(),
			"DrugBank","SILVER","curated","IC","ECO:0000305", "curator inference used in manual assertion"),
	KEGGPATHWAY("KEGGPathway",  "pathway name", AnnotationCategory.PATHWAY.getDbAnnotationTypeName(), 
			"KEGG_PTW","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion"),
	TCDB("TCDB",  "family name", AnnotationCategory.TRANSPORT_ACTIVITY.getDbAnnotationTypeName(),
			"TCDB","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion")
	;

	private final String xrefDbName; // refers to an existing cv_name in cv_databases table
	private final String annotCat;
	private final String srcName;    // refers to an existing cv_name in cv_datasources table
	private final String xrefPropName;
	private final String qualityQualifier;
	private final String assignmentMethod;
	private final String qualifierType;
	private final String ecoAC;
	private final String ecoName;
	private final String 

    Xref2Annotation(final String xrefDbName,
			final String xrefPropName, final String annotCat, final String srcName,
			final String qualityQualifier, final String assignMethod, final String qualifierType, String ecoAC, String ecoName) {
		
		this.xrefDbName=xrefDbName;
		this.xrefPropName=xrefPropName;
		this.annotCat=annotCat;
		this.srcName=srcName;
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

	public String getXrefDbName() {
		return xrefDbName;
	}
	public String getXrefPropName() {
		return xrefPropName;
	}

	public String getAnnotCat() {
		return annotCat;
	}

	public String getSrcName() {
		return srcName;
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
        return "EvidenceCodeOntologyCv";
	}

	public static Xref2Annotation getByDatabaseName(String dbName) {
		for (Xref2Annotation m : Xref2Annotation.values()) {
			if(m.xrefDbName.equals(dbName)) return m;
		}
		throw new RuntimeException("Could not find XrefAnnotationMapping for database name: " + dbName);
	}

	public static boolean hasName(String name) {

		for (Xref2Annotation m : Xref2Annotation.values()) {
			if(m.xrefDbName.equals(name)) return true;
		}

		return false;
	}
}
