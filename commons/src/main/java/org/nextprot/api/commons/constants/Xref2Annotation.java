package org.nextprot.api.commons.constants;

/**
 * Description: Used to turn some XRefs into Annotations<br> * 
 * @author Pam
 */

public enum Xref2Annotation  {

	ORPHANET(84,"Orphanet", "disease", AnnotationCategory.DISEASE.getDbAnnotationTypeName(),2,"Uniprot","GOLD","curated", "IC","ECO:0000305", "curator inference used in manual assertion"),
	REACTOME(112, "Reactome", "pathway name", AnnotationCategory.PATHWAY.getDbAnnotationTypeName(), 2, "Uniprot","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion"),
	DRUGBANK(27,"DrugBank", "generic name", AnnotationCategory.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName(),2,"Uniprot","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion"),
	KEGGPATHWAY(186,"KEGGPathway",  "pathway name", AnnotationCategory.PATHWAY.getDbAnnotationTypeName(), 1, "NextProt","GOLD","curated","IC","ECO:0000305", "curator inference used in manual assertion"), // ALTERNATIVE SOURCE = 2 , "NextProt integration"
	;

	private final int xrefDbId; 
	private final String xrefDbName;
	private final String annotCat;
	private final int srcId;
	private final String srcName;
	private final String xrefPropName;
	private final String qualityQualifier;
	private final String assignmentMethod;
	private final String qualifierType;
	private final String ecoAC;
	private final String ecoName;

    Xref2Annotation(final int xrefDbId, final String xrefDbName,
			final String xrefPropName, final String annotCat, final int srcId, final String srcName,
			final String qualityQualifier, final String assignMethod, final String qualifierType, String ecoAC, String ecoName) {
		
		this.xrefDbId=xrefDbId;
		this.xrefDbName=xrefDbName;
		this.xrefPropName=xrefPropName;
		this.annotCat=annotCat;
		this.srcId=srcId;
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

	public int getXrefDbId() {
		return xrefDbId;
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

	public int getSrcId() {
		return srcId;
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
