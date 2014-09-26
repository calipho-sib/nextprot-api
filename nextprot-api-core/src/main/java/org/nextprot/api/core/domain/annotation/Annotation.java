package org.nextprot.api.core.domain.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.core.domain.OWLAnnotationCategory;

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
	
	private OWLAnnotationCategory owlAnnotCat;

	private List<AnnotationEvidence> evidences;

	private Map<String, AnnotationIsoformSpecificity> targetingIsoformsMap;

	private List<AnnotationProperty> properties;

	final static Map<String, String> commonExpressionPredicat= new HashMap<String, String>();
	
	static{
		
		//
		// map expressionLevel in 3 categories : negative, positive for consensus and mixing when no consensus exist  
		commonExpressionPredicat.put("", 	   	"variableExpression");
		commonExpressionPredicat.put("High",   	"expression");
		commonExpressionPredicat.put("Low",    	"expression");
		commonExpressionPredicat.put("Medium", 	"expression");
		commonExpressionPredicat.put("Positive","expression");
		commonExpressionPredicat.put("Negative","negativeExpression");
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
		return owlAnnotCat.getRdfTypeName();
	}

	public String getRdfPredicate() {
		return owlAnnotCat.getRdfPredicate();
	}
	
	public List<String> getParentPredicates() {
		List<String> list = new ArrayList<String>();
		for (OWLAnnotationCategory cat : owlAnnotCat.getAllParents()) list.add(cat.getRdfPredicate());
		return list;
	}
	
	public void setCategory(String category) {
		this.category = category;
		this.owlAnnotCat=OWLAnnotationCategory.getByDbAnnotationTypeName(category);
	}

	public AnnotationVariant getVariant() {
		return variant;
	}

	public void setVariant(AnnotationVariant variant) {
		this.variant = variant;
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
		return this.targetingIsoformsMap.get(isoformName).getFirstPosition();
	}
	
	public String getSpecificityForIsoform(String isoformName) {
		return this.targetingIsoformsMap.get(isoformName).getSpecificity();
	}
	
	public int getEndPositionForIsoform(String isoformName) {
		return this.targetingIsoformsMap.get(isoformName).getLastPosition();
	}
	
	/**
	 * select consensus between positive and negative expression, or no consensus 
	 * @return positive consensus: expression, negative consensus: negativeExpression or no consensus: mixingExpression
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
			if(!level.equals(commonExpressionPredicat.get(e.getExpressionLevel())))
				return commonExpressionPredicat.get("");
		}
		return level;
	}

}
