package org.nextprot.api.core.domain.annotation;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.IsoformSpecific;
import org.nextprot.api.core.domain.IsoformSpecificity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Annotation implements Serializable, IsoformSpecific {

	
	private static final long serialVersionUID = -1576387963315643702L;

	private String uniqueName;

	private String cvTermAccessionCode;

	private String cvTermName;

	private String cvApiName;

	private String description;

	private String category;

	private String qualityQualifier;

	private long annotationId;

	private AnnotationVariant variant;

	private String synonym;
	
	private List<String> synonyms;

	private AnnotationApiModel apiCategory;

	private List<AnnotationEvidence> evidences;

	@Deprecated // use target isoform map
	private Map<String, AnnotationIsoformSpecificity> targetingIsoformsMap;
	
	private Map<String, IsoformSpecificity> targetIsoformsMap;

	private List<AnnotationProperty> properties;

	private BioObject bioObject;
	
	private DbXref parentXref; // non null only when annotation is built from an xref (see AnnotationServiceImpl.getXrefsAsAnnotationsByEntry()
	
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

	public String getCvApiName() {
		return cvApiName;
	}

	public void setCvApiName(String cvApiName) {
		this.cvApiName = cvApiName;
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

	public String getApiTypeName() {
		if(apiCategory != null){
			return apiCategory.getApiTypeName();
		}else return null;
	}

	public String getRdfPredicate() {
		if(apiCategory != null){
			return apiCategory.getRdfPredicate();
		}else return null;
	}
	
	public AnnotationApiModel getAPICategory() {
		return apiCategory;
	}
	
	public List<String> getParentPredicates() {
		if(apiCategory!= null){
			List<String> list = new ArrayList<String>();
			for (AnnotationApiModel cat : apiCategory.getAllParentsButRoot()) list.add(cat.getRdfPredicate());
			return list;
		}else return null;
	}
			
	public void setCategory(String category) {
		this.category = category;
		this.apiCategory=AnnotationApiModel.getByDbAnnotationTypeName(category);
	}
	
	public void setCategoryOnly(String category) {
		this.category = category;
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

	public List<String> getAllSynonyms() {
		return synonyms;
	}

	public void setAllSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	@Override
	public boolean isSpecificForIsoform(String isoform) {
		return targetingIsoformsMap.containsKey(isoform);
	}

	public boolean isAnnotationPositionalForIsoform(String isoform) {
		if(isSpecificForIsoform(isoform)){
			return targetingIsoformsMap.get(isoform).isPositional();
		}else return false;
	}

	@Deprecated //Use setTargetIsoformsMap instead
	public void setTargetingIsoforms(List<AnnotationIsoformSpecificity> targetingIsoforms) {
		this.targetingIsoformsMap = new HashMap<String, AnnotationIsoformSpecificity>();
		for (AnnotationIsoformSpecificity isospecAnnot : targetingIsoforms) {
			targetingIsoformsMap.put(isospecAnnot.getIsoformName(), isospecAnnot);
		}
	}
	
	//This new method replaces setTargetingIsoforms
	public void setTargetIsoformsMap(List<IsoformSpecificity> targetingIsoforms) {
		this.targetIsoformsMap = new HashMap<String, IsoformSpecificity>();
		for (IsoformSpecificity isospecAnnot : targetingIsoforms) {
			targetIsoformsMap.put(isospecAnnot.getIsoformAc(), isospecAnnot);
		}
	}

	@Deprecated
	public Map<String, AnnotationIsoformSpecificity> getTargetingIsoformsMap() {
		return targetingIsoformsMap;
	}
	
	public Map<String, IsoformSpecificity> getTargetIsoformsMap() {
		return targetIsoformsMap;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public int getStartPositionForIsoform(String isoformName) {
		if(targetingIsoformsMap != null){
			Preconditions.checkArgument(targetingIsoformsMap.containsKey(isoformName), isoformName + " is not contained");
			return this.targetingIsoformsMap.get(isoformName).getFirstPosition();
		}else {
			Preconditions.checkArgument(targetIsoformsMap.containsKey(isoformName), isoformName + " is not contained");
			return this.targetIsoformsMap.get(isoformName).getPositions().get(0).getFirst();
		}
	}
	
	public String getSpecificityForIsoform(String isoformName) {
		return this.targetingIsoformsMap.get(isoformName).getSpecificity();
	}

	public BioObject getBioObject() {
		return bioObject;
	}

	public void setBioObject(BioObject bioObject) {
		this.bioObject = bioObject;
	}

	public int getEndPositionForIsoform(String isoformName) {
		if(targetingIsoformsMap != null){
		Preconditions.checkArgument(targetingIsoformsMap.containsKey(isoformName));
			return this.targetingIsoformsMap.get(isoformName).getLastPosition();
		}else {
			Preconditions.checkArgument(targetIsoformsMap.containsKey(isoformName), isoformName + " is not contained");
			return this.targetIsoformsMap.get(isoformName).getPositions().get(0).getSecond();
		}
	}
	
	/**
	 * Return true if annotation has at least one evidence showing any kind of detection (low, medium, high or positive) else false
	 *
	 * @return an optional boolean or absent if no expression info
	 */
	public Optional<Boolean> isExpressionLevelDetected() {

		Optional<Boolean> booleanOptional = Optional.absent();

		if (evidences != null) {

			for (AnnotationEvidence evidence : evidences) {

				String level = evidence.getExpressionLevel();

				if (level != null) {

					switch (level) {

						case "low":
						case "medium":
						case "high":
						case "positive":
							return Optional.of(Boolean.TRUE);
						default:
							booleanOptional = Optional.of(Boolean.FALSE);
					}
				}
			}
		}

		return booleanOptional;
	}
}
