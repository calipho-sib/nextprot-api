package org.nextprot.api.commons.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.utils.StringUtils;

/**
 * Description: Used to turn some XRefs into Annotations<br> * 
 * @author Pam
 */

public enum XrefAnnotationMapping  {

	//TODO: get the mapping parameters validated
	ORPHANET(84,"Orphanet", "disease", AnnotationApiModel.DISEASE.getDbAnnotationTypeName(),2,"Uniprot","GOLD","EXP","curated"),
	REACTOME(112, "Reactome", "pathway name", AnnotationApiModel.PATHWAY.getDbAnnotationTypeName(), 2, "Uniprot","SILVER","EXP","curated"), 
	DRUGBANK(27,"DrugBank", "generic name", AnnotationApiModel.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName(),2,"Uniprot","GOLD","EXP","curated"),
	KEGGPATHWAY(186,"KEGGPathway",  "pathway name", AnnotationApiModel.PATHWAY.getDbAnnotationTypeName(), 1, "NextProt","GOLD","EXP","curated"), // ALTERNATIVE SOURCE = 2 , "NextProt integration"
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
		
	private XrefAnnotationMapping(final int xrefDbId, final String xrefDbName, 
			final String xrefPropName, final String annotCat, final int srcId, final String srcName,
			final String qualityQualifier, final String assignMethod, final String qualifierType) {
		
		this.xrefDbId=xrefDbId;
		this.xrefDbName=xrefDbName;
		this.xrefPropName=xrefPropName;
		this.annotCat=annotCat;
		this.srcId=srcId;
		this.srcName=srcName;
		this.qualityQualifier=qualityQualifier;
		this.qualifierType=qualifierType;
		this.assignmentMethod=assignMethod;
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
		
	public static XrefAnnotationMapping getByDatabaseName(String dbName) {
		for (XrefAnnotationMapping m : XrefAnnotationMapping.values()) {
			if(m.xrefDbName.equals(dbName)) return m;
		}
		throw new RuntimeException("Could not find XrefAnnotationMapping for database name: " + dbName);
	}
	

}
