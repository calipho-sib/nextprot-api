package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.util.*;

public class AnnotationEvidence implements Serializable {

	private static final long serialVersionUID = 2856324820767690302L;

	// map uniprot evidence code with ECO
	final static Map<String, String> evidenceInfo = new HashMap<>();

	// evidence properties mapping
	private Map<String, String> propertiesMap= new HashMap<>();

	static {
		
		// map uniprot evidence code with ECO
		evidenceInfo.put("UNKNOWN", "EXP");
		evidenceInfo.put("PROBABLE", "IC");   // IC=inferred by curator
		evidenceInfo.put("POTENTIAL", "IEA"); // IEA=Inferred from Electronic Annotation
		evidenceInfo.put("BY_SIMILARITY", "ISS"); // ISS=Inferred from Sequence or Structural Similarity
	}

	private long resourceId;
	private String resourceType;
	private String resourceAccession;
	private String resourceDb;
	private String resourceDesc;
	private String publication_md5;
	private Long experimentalContextId;
	private long annotationId;
	private boolean isNegativeEvidence;
	private String qualifierType;
	private String qualityQualifier;
	private long evidenceId;
	private String assignedBy;
	private String assignmentMethod;
	private String evidenceCodeAC;
	private String evidenceCodeName;
	private String evidenceCodeOntology;

	public String getEvidenceCodeAC() {
		return evidenceCodeAC;
	}

	public void setEvidenceCodeAC(String evidenceCodeAC) {
		this.evidenceCodeAC = evidenceCodeAC;
	}

	public String getEvidenceCodeName() {
		return evidenceCodeName;
	}

	public void setEvidenceCodeName(String evidenceCodeName) {
		this.evidenceCodeName = evidenceCodeName;
	}

	public Long getExperimentalContextId() {
		return experimentalContextId;
	}

	public void setExperimentalContextId(Long experimentalContextId) {
		this.experimentalContextId = experimentalContextId;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceAssociationType() {
		return resourceAssociationType;
	}

	public void setResourceAssociationType(String resourceAssociationType) {
		this.resourceAssociationType = resourceAssociationType;
	}

	private String resourceAssociationType;

	public long getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(long annotationId) {
		this.annotationId = annotationId;
	}

	public boolean isNegativeEvidence() {
		return isNegativeEvidence;
	}

	/**
	 * Returns the negative flag of the evidence after taking into account 
	 * the value of the negative isoform specificity
	 * @param isoAC an isoform accession
	 * @return
	 */
	public boolean isNegativeEvidence(String isoAC) {
		String ac = (isoAC.startsWith("NX_") ? isoAC.substring(3) : isoAC);
		if (getNegativeIsoformSpecificity()==null) {
			return isNegativeEvidence;
		} else {
			return (getNegativeIsoformSpecificity().contains(ac) ? ! isNegativeEvidence : isNegativeEvidence);
		}
	}

	public void setNegativeEvidence(boolean isNegativeEvidence) {
		this.isNegativeEvidence = isNegativeEvidence;
	}

	public String getQualifierType() {
		if (evidenceInfo.containsKey(qualifierType))
			return evidenceInfo.get(qualifierType);
		return qualifierType;
	}

	public void setQualifierType(String qualifierType) {
		this.qualifierType = qualifierType;
	}

	/**
	 * Returns either database or publication //TODO should we put this on enum? 
	 * @return
	 */
	public String getResourceType() {
		return resourceType;
	}
	
	public boolean isResourceAXref() {
		return ("database".equals(resourceType));
	}

	public boolean isResourceAPublication() {
		return ("publication".equals(resourceType));
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceAccession() {
		return resourceAccession;
	}

	public void setResourceAccession(String resourceAccession) {
		this.resourceAccession = resourceAccession;
	}

	public void setResourceDb(String db) {
		this.resourceDb=db;
	}
	public String getResourceDb() {
		return resourceDb;
	}
	
	public void setResourceDescription(String desc) {
		this.resourceDesc=desc;
	}

	public String getResourceDescription() {
		return resourceDesc;
	}

	/**
	 * we handle multiple property values by joining them with comma
	 * 
	 * @param properties
	 */
	public void setProperties(List<AnnotationEvidenceProperty> properties) {
		propertiesMap.clear();
		for (AnnotationEvidenceProperty prop : properties) {
			String name = prop.getPropertyName();
			String oldVal =  (propertiesMap.containsKey(name) ? propertiesMap.get(name) + ","  : "" );
			propertiesMap.put(name, oldVal + prop.getPropertyValue());
		}
	}

	/**
	 * Determines if an evidence has to be taken into account for xml / ttl, ... exports.
	 * We should only export, publish evidences meeting the following requirements:
	 * - evidences with type="evidence"
	 * OR
	 * - evidences with type="source" and assignedBy="Uniprot"
	 * See also http://issues.isb-sib.ch/browse/CALIPHOMISC-147
	 * @return
	 */
	public boolean isValid() {
		String typ = getResourceAssociationType();
		if (typ.equals("evidence")) return true;
		if (typ.equals("source") && getAssignedBy()!=null && getAssignedBy().equals("Uniprot")) return true;
		return false;
	}
	
	/**
	 * Determines if an evidence is to be applied to an isoform
	 * See also details in http://issues.isb-sib.ch/browse/CALIPHOMISC-145
	 * @param isoAC
	 * @return
	 */
	public boolean appliesToIsoform(String isoAC) {
		if (!isValid()) return false;
		boolean result = false;
		String ac = (isoAC.startsWith("NX_") ? isoAC.substring(3) : isoAC);
		if (getIsoformSpecificity() == null && getNegativeIsoformSpecificity() == null) result = true;		
		if (getIsoformSpecificity() != null && getIsoformSpecificity().contains(ac)) result = true;
		if (getNegativeIsoformSpecificity() != null && getNegativeIsoformSpecificity().contains(ac)) result = true;
		return result;
	}
	
	public long getEvidenceId() {
		return evidenceId;
	}

	/**
	 * Returns an id for the evidence based on the database identifier but adds a prefix "nis" when the evidence
	 * is said to have a negative isoform specificity for the isoform identified by isoAC.
	 * This is useful because in such a case the negative flag of the evidence should be inversed. It allows to
	 * split an evidence into 2 distinct evidences in the context of ttl file generation 
	 * See also details in http://issues.isb-sib.ch/browse/CALIPHOMISC-145
	 * @param isoAC an isoform accession
	 * @return
	 */
	public String getEvidenceId(String isoAC) {
		String baseId = "" + evidenceId;
		String ac = (isoAC.startsWith("NX_") ? isoAC.substring(3) : isoAC);
		if (getNegativeIsoformSpecificity()==null) {
			return baseId;
		} else {
			return (getNegativeIsoformSpecificity().contains(ac) ? 	"nis"+ baseId : baseId);
		}
	}

	/*
	 * 		String ac = (isoAC.startsWith("NX_") ? isoAC.substring(3) : isoAC);
		if (getNegativeIsoformSpecificity()==null) {
			return isNegativeEvidence;
		} else {
			return (getNegativeIsoformSpecificity().contains(ac) ? ! isNegativeEvidence : isNegativeEvidence);
		}

	 */
	
	public void setEvidenceId(long evidenceId) {
		this.evidenceId = evidenceId;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public String getPublicationMD5() {
		return publication_md5;
	}

	public void setPublicationMD5(String md5) {
		this.publication_md5 = md5;
	}

	public String getQualityQualifier() {
		return qualityQualifier;
	}

	public void setQualityQualifier(String qualityQualifier) {
		this.qualityQualifier = qualityQualifier;
	}

	/**
	 * 
	 * @return a set of property names related to the evidence and that are allowed to be shown
	 */
	public Set<String> getPropertiesNames() {
		/*
		// do an intersection between properties we want to show and properties we have
		Set<String> propsOk = new HashSet<>(Arrays.asList("expressionLevel","antibodies acc"));
		propsOk.retainAll(propertiesMap.keySet());
		// return the intersection
		return propsOk;
		*/
		return propertiesMap==null ? new HashSet<String>() : propertiesMap.keySet();
	}
	
	
	public String getPropertyRawValue(String name) {
		return propertiesMap.get(name);
	}
	
	
	/**
	 * 
	 * @param name a property name
	 * @return a string representing the property value associated to name
	 */
	public String getPropertyValue(String name) {
		// special cases first
		if ("expressionLevel".equals(name)) return getExpressionLevel();
		if ("integrationLevel".equals(name)) return getIntegrationLevel();
		//...
		// general case finally
		return propertiesMap.get(name);
	}
	
	/**
	 * deploy properties,
	 * 
	 * select distinct property_value from
	 * nextprot.annotation_resource_assoc_properties where property_name =
	 * 'integrationLevel' order by property_value
	 */
	private String extractProperty(String propertyName) {
		
		return propertiesMap.get(propertyName);

	}


	/**
	 * See http://issues.isb-sib.ch/browse/CALIPHOMISC-142 for more details
	 * 
	 * @return 2 possible values: colocalizes_with / contributes_to
	 */
	@Deprecated
	public String getGoQualifier() {
		return extractProperty("go_qualifier");
	}

	/**
	 * Multiple values for same evidence / property name are comma separated.
	 * The value of this property is a list of isoform accessions (without NX_ prefix).
	 * When an isoform is cited in this property it means that the related annotation assertion is FALSE for this isoform.
	 * In other words the negative property of the evidence has to be inversed for this isoform. 
	 * See also http://issues.isb-sib.ch/browse/CALIPHOMISC-145
	 * @return a list of isoform accession without NX_ prefix or null if no specificity is known
	 */
	public String getNegativeIsoformSpecificity() {
		return extractProperty("negative_isoform_specificity");
	}

	/**
	 * CL = cell line, multiple values possible for same evidence / prop name !
	 * Will be replaced by term in experimental context
	 * 
	 * @return Example: NB4, HL60, ...
	 */
	@Deprecated
	public String getCL() {
		return extractProperty("CL");
	}

	/**
	 * Multiple values for same evidence / property name are comma separated.
	 * The value of this property is a list of isoform accessions (without NX_ prefix).
	 * When an isoform is cited in this property it means that the related annotation assertion is TRUE for this isoform.
	 * See also http://issues.isb-sib.ch/browse/CALIPHOMISC-145
	 * @return a list of isoform accession without NX_ prefix or null if no specificity is known
	 */
	public String getIsoformSpecificity() {
		return extractProperty("isoform_specificity");
	}

	/**
	 * used for PTM assigned by neXtProt (from proteomics papers) only on
	 * peptide annotation. is not displayed
	 * 
	 * not used in ttl template
	 * @return
	 */
	public String getReferenceIdentifierAccession() {
		return extractProperty("reference identifier acc");
	}

	/**
	 * sample id: used for cosmic variants, keep trace of sample_ids described
	 * in a publication 
	 * 
	 * is not displayed
	 * 
	 * not used in ttl template
	 * @return
	 */
	@Deprecated
	public String getSampleId() {
		return extractProperty("sample id");
	}

	/**
	 * SP = sample preparation, multiple values possible for same evidence /
	 * prop name ! Will be replaced by term in experimental context
	 * 
	 * not used in ttl template

	 * @return Example: Lys-CSC, Cys-Glyco-CSC,Lys-CSC
	 */
	@Deprecated
	public String getSP() {
		return extractProperty("SP");
	}
	@Deprecated
	public String getExpressionLevel() {
		return extractProperty("expressionLevel");
	}
	@Deprecated
	public String getIntegrationLevel() {
		return extractProperty("integrationLevel");
	}

	
	public String getIntensity() {
		return extractProperty("intensity");
	}
	
	public String getProteinOrigin_TODEBUG() {
		return extractProperty("protein-origin");
	}
	
	public String getSourceAccession_TODEBUG() {
		return extractProperty("source-accession");
	}
	
	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}

	public String getEvidenceCodeOntology() {
		return evidenceCodeOntology;
	}

	public void setEvidenceCodeOntology(String ontology) {
		this.evidenceCodeOntology = ontology;
	}
}
