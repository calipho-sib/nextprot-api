package org.nextprot.api.core.domain.annotation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.commons.constants.AnnotationPropertyApiModel;
import org.nextprot.api.core.domain.DbXref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Annotation implements Serializable {

	private static final long serialVersionUID = -1576387963315643702L;

	private String uniqueName;

	private String cvTermAccessionCode;

	private String cvTermName;
		
	private String description;

	private String category;

	private String qualityQualifier;

	private long annotationId;

	private AnnotationVariant variant;

	private String synonym;
	
	private AnnotationApiModel apiCategory;

	private List<AnnotationEvidence> evidences;

	private Map<String, AnnotationIsoformSpecificity> targetingIsoformsMap;

	private List<AnnotationProperty> properties;
	
	private DbXref parentXref; // non null only when annotation is built from an xref (see AnnotationServiceImpl.getXrefsLikeAnnotations()

	final static Map<String, String> commonExpressionPredicat= new HashMap<String, String>();
	
	static{
		
		/*
		 * i changed the predicate names to be compatible with predicate hierarchy
		 */
		
		commonExpressionPredicat.put("", 	   	"detectedExpression");
		commonExpressionPredicat.put("High",   	"detectedExpression");
		commonExpressionPredicat.put("Low",    	"detectedExpression");
		commonExpressionPredicat.put("Medium", 	"detectedExpression");
		commonExpressionPredicat.put("Positive","detectedExpression");
		commonExpressionPredicat.put("Negative","undetectedExpression");
	}	
	
	
	public String toString() {
		return uniqueName + ": "  + 
				"cvTermAccessionCode:" + cvTermAccessionCode +
				" - cvTermName:" + cvTermName +
				" - description:"  + description;
	}
	
	public DbXref getParentXref() {
		return parentXref;
	}

	public void setParentXref(DbXref parentXref) {
		this.parentXref = parentXref;
	}
	
	public List<AnnotationEvidence> getEvidences() {
		return evidences;
	}

	public void setEvidences(List<AnnotationEvidence> evidences) {
		this.evidences = evidences;
	}

	public String getCvTermAccessionCode() {
		return cvTermAccessionCode;
	}	
		

	public void setCvTermAccessionCode(String cvTermAccessionCode) {
		this.cvTermAccessionCode = cvTermAccessionCode;
	}

	public String getCvTermName() {
		return cvTermName;
	}

	public void setCvTermName(String cvTermName) {
		this.cvTermName = cvTermName;
	}

	public String getQualityQualifier() {
		return qualityQualifier;
	}

	public void setQualityQualifier(String qualityQualifier) {
		this.qualityQualifier = qualityQualifier;
	}

	public long getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(long annotationId) {
		this.annotationId = annotationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public String getRdfTypeName() {
		return apiCategory.getRdfTypeName();
	}

	public String getRdfPredicate() {
		return apiCategory.getRdfPredicate();
	}
	
	public AnnotationApiModel getAPICategory() {
		return apiCategory;
	}
	
	public List<String> getParentPredicates() {
		List<String> list = new ArrayList<String>();
		for (AnnotationApiModel cat : apiCategory.getAllParentsButRoot()) list.add(cat.getRdfPredicate());
		return list;
	}
			
	public void setCategory(String category) {
		this.category = category;
		this.apiCategory=AnnotationApiModel.getByDbAnnotationTypeName(category);
	}

	public AnnotationVariant getVariant() {
		return variant;
	}

	public void setVariant(AnnotationVariant variant) {
		this.variant = variant;
	}

	/*
	 * returns API model of a property of this annotation 
	 */
	public AnnotationPropertyApiModel getPropertyApiModel(String dbName) {
		return this.apiCategory.getPropertyByDbName(dbName);
	}
	
	/*
	 * returns API model of a property of this annotation 
	 */
	public AnnotationPropertyApiModel getPropertyApiModel(AnnotationProperty prop) {
		return this.apiCategory.getPropertyByDbName(prop.getName());
	}

	public List<AnnotationProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<AnnotationProperty> properties) {
		this.properties = properties;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public boolean isAnnotationValidForIsoform(String isoform) {
		return targetingIsoformsMap.containsKey(isoform);
	}

	public boolean isAnnotationPositionalForIsoform(String isoform) {
		if(isAnnotationValidForIsoform(isoform)){
			return targetingIsoformsMap.get(isoform).isPositional();
		}else return false;
	}

	public void setTargetingIsoforms(List<AnnotationIsoformSpecificity> targetingIsoforms) {
		this.targetingIsoformsMap = new HashMap<String, AnnotationIsoformSpecificity>();
		for (AnnotationIsoformSpecificity isospecAnnot : targetingIsoforms) {
			targetingIsoformsMap.put(isospecAnnot.getIsoformName(), isospecAnnot);
		}
	}

	public Map<String, AnnotationIsoformSpecificity> getTargetingIsoformsMap() {
		return targetingIsoformsMap;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	public int getStartPositionForIsoform(String isoformName) {
		Preconditions.checkArgument(targetingIsoformsMap.containsKey(isoformName));
		return this.targetingIsoformsMap.get(isoformName).getFirstPosition();
	}
	
	public String getSpecificityForIsoform(String isoformName) {
		return this.targetingIsoformsMap.get(isoformName).getSpecificity();
	}
	
	public int getEndPositionForIsoform(String isoformName) {
		Preconditions.checkArgument(targetingIsoformsMap.containsKey(isoformName));
		return this.targetingIsoformsMap.get(isoformName).getLastPosition();
	}
	
	/**
	 * pam 16.11. 2015, harmonization with data model:
	 * selects expression level consensus:
	 * - detectedExpression if any evidence is either low, high, medium or positive
	 * - undetectedExpression if all evidences are negative or not detected
	 * - null if no data is found 
	 * @return "detectedExpression", "undetectedExpression" or null
	 */
	public String getConsensusExpressionLevelPredicat(){
		String level="";
		// make sure we have evidences otherwise error breaks ttl generation
		if (evidences.size()==0) 
			return null;
		// check if there is expression info
		if ((level=evidences.get(0).getExpressionLevel())==null)
			return null;
		level=commonExpressionPredicat.get(level);
		for(AnnotationEvidence e:evidences){
			if(!level.equals(commonExpressionPredicat.get(e.getExpressionLevel()))) {
				return commonExpressionPredicat.get(""); // default 
			}
		}
		return level;
	}

}
