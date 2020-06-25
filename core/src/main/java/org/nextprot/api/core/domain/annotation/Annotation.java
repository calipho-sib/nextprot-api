package org.nextprot.api.core.domain.annotation;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.IsoformSpecific;
import org.nextprot.api.core.service.annotation.comp.AnnotationPropertyComparator;

import java.io.Serializable;
import java.util.*;

// I would love to encapsulate what varies and only keep what is common there
//I would love to do @JsonInclude(Include.NON_NULL)
public class Annotation implements Serializable, IsoformSpecific {

	private List<String> subjectComponents;
	
	@Deprecated
	private String annotationName;
	
	private String annotationHash;

	private List<String> isoformsDisplayedAsSpecific;

	
	public List<String> getIsoformsDisplayedAsSpecific() {
		return isoformsDisplayedAsSpecific;
	}

	public void setIsoformsDisplayedAsSpecific(List<String> isoformsDisplayedAsSpecific) {
		this.isoformsDisplayedAsSpecific = isoformsDisplayedAsSpecific;
	}

	private static final long serialVersionUID = 1L;

	private List<String> synonyms;

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	private String uniqueName;

	private String cvTermAccessionCode;

	private String cvTermName;

	private String cvTermType;

	private String cvTermDescription;

	private String cvApiName;

	private String description;

	// TODO: apiCategory already exist (DRY)
	private String category;

	private String qualityQualifier;

	private long annotationId;

	private AnnotationVariant variant;

	// TODO: synonyms already exist (DRY)
	private String synonym;
	
	private AnnotationCategory apiCategory;

	private List<AnnotationEvidence> evidences;

	private Set<String> indirectEvidenceRefList = null;

	private final Map<String, AnnotationIsoformSpecificity> targetingIsoformsMap = new TreeMap<>();

	private final Map<String, Collection<AnnotationProperty>> properties = new TreeMap<>();

	private BioObject bioObject;
	
	private DbXref parentXref; // non null only when annotation is built from an xref (see AnnotationServiceImpl.getXrefsAsAnnotationsByEntry()

	private List<String> relatedTo;

	public String toString() {
		return uniqueName + ": "  + 
				"cvTermAccessionCode:" + cvTermAccessionCode +
				" - cvTermName:" + cvTermName +
				" - description:"  + description;
	}
	
	
	/**
	 * Pam
	 * Only ModificationEffect annotations directly apply to proteoforms.
	 * ProteinProperty and MammalianPhenotype also applies to proteoforms BUT indirectly.
	 * They only appear as the object of a ModificationEffect proteoform annotation (in annot.bio-object.annotationHash).
	 * @return true if a proteoform (modified isoform) is the subject of the annotation 
	 */
	public boolean isProteoformAnnotation() {
		return (subjectComponents!=null && ! subjectComponents.isEmpty());
	}

	/**
	 * Pam
	 * Some annotation categories should not be linked to an isoform or entry but only to a proteoform.
     * They only appear as the object of a proteoform ModificationEffect annotation.
     * I use the term WildType instead of Isoform to avoid confusions. 
	 * @return true if annotation applies to unmodified isoforms (wild type = isoform)
	 */
	public boolean isWildTypeAnnotation() {
		if (AnnotationCategory.PROTEIN_PROPERTY == this.apiCategory) return false;
		if (AnnotationCategory.MAMMALIAN_PHENOTYPE == this.apiCategory) return false;
		if (AnnotationCategory.PHENOTYPIC_VARIATION ==  this.apiCategory) return false;
		return true;
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

	public String getCvTermType() {
		return cvTermType;
	}

	public void setCvTermType(String cvTermType) {
		this.cvTermType = cvTermType;
	}

	public String getCvTermDescription() {
		return cvTermDescription;
	}

	public void setCvTermDescription(String cvTermDescription) {
		this.cvTermDescription = cvTermDescription;
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
	

	public String getCategoryName() {
		return getApiTypeName();
	}

	// Called from Velocity templates
	public String getApiTypeName() {
		if(apiCategory != null){
			return apiCategory.getApiTypeName();
		}else return null;
	}

	// Called from Velocity templates
	public String getRdfPredicate() {
		if(apiCategory != null){
			return apiCategory.getRdfPredicate();
		}else return null;
	}
	
	public AnnotationCategory getAPICategory() {
		return apiCategory;
	}

	// Called from Velocity templates
	public List<String> getParentPredicates() {
		if(apiCategory!= null){
			List<String> list = new ArrayList<>();
			for (AnnotationCategory cat : apiCategory.getAllParentsButRoot()) list.add(cat.getRdfPredicate());
			return list;
		}else return null;
	}
	
	public void setAnnotationCategory(AnnotationCategory category) {
		//wtf???? names are not coherent...
		this.apiCategory= category;
		this.category = category.getDbAnnotationTypeName();
	}
	
	@Deprecated
	//Use setAnnotationCategory instead
	public void setCategory(String category) {
		this.category = category;
		this.apiCategory= AnnotationCategory.getByDbAnnotationTypeName(category);
	}

	public AnnotationVariant getVariant() {
		return variant;
	}

	public void setVariant(AnnotationVariant variant) {
		this.variant = variant;
	}

    /**
     * Get an immutable map of AnnotationProperty collection
     */
	public Map<String, Collection<AnnotationProperty>> getPropertiesMap() {
		return Collections.unmodifiableMap(properties);
	}

    /**
     * Get an immutable collection of AnnotationProperty by key
     * @param key the key to access properties
     */
    public Collection<AnnotationProperty> getPropertiesByKey(String key) {
    	Collection<AnnotationProperty> props = properties.get(key);
    	if (props==null) props = new ArrayList<>();
        return Collections.unmodifiableCollection(props);
    }

    /**
     * Get an immutable collection of AnnotationProperty
     */
    public Collection<AnnotationProperty> getProperties() {

        Collection<AnnotationProperty> props = new ArrayList<>();

        for (Collection<AnnotationProperty> collection : properties.values()) {

            props.addAll(collection);
        }

        return Collections.unmodifiableCollection(props);
    }

	/**
	 * Get an immutable collection of AnnotationProperty with the exception of propertyName
     */
	public Collection<AnnotationProperty> getPropertiesExceptName(String propertyName) {

		Collection<AnnotationProperty> filterProps = new ArrayList<>();

		Collection<AnnotationProperty> props = getProperties();

		for (AnnotationProperty prop : props) {
			if(!prop.getName().equals(propertyName)) {

				filterProps.add(prop);
			}
		}

		return Collections.unmodifiableCollection(filterProps);
	}

    /**
     * Add properties into the map
     * @param props properties to add
     */
	public void addProperties(Collection<AnnotationProperty> props) {

        for (AnnotationProperty property : props) {

        	addProperty(property);
        }
	}

	public void addProperty(AnnotationProperty property) {

		String propertyName = property.getName();
		if (!properties.containsKey(propertyName)) {
			properties.put(propertyName, new TreeSet<>(new AnnotationPropertyComparator()));
		}
		properties.get(propertyName).add(property);
	}
	
	
	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
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

	public void addTargetingIsoforms(List<AnnotationIsoformSpecificity> targetingIsoforms) {
		for (AnnotationIsoformSpecificity isospecAnnot : targetingIsoforms) {
			targetingIsoformsMap.put(isospecAnnot.getIsoformAccession(), isospecAnnot);
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

	/** @return the first position or null if unknown */
	public Integer getStartPositionForIsoform(String isoformName) {

		if (targetingIsoformsMap.containsKey(isoformName))
			return this.targetingIsoformsMap.get(isoformName).getFirstPosition();
		return null;
	}

	/** @return the last position or null if unknown */
	public Integer getEndPositionForIsoform(String isoformName) {
		if (targetingIsoformsMap.containsKey(isoformName)) {
			//System.out.println(isoformName);
			//System.out.println("start: " + this.targetingIsoformsMap.get(isoformName).getFirstPosition());
			//System.out.println(" end: " + this.targetingIsoformsMap.get(isoformName).getLastPosition());
			return this.targetingIsoformsMap.get(isoformName).getLastPosition();
		}
		return null;
	}

	// Called from Velocity templates
	public String getSpecificityForIsoform(String isoformName) {
		return this.targetingIsoformsMap.get(isoformName).getSpecificity();
	}
	
	public BioObject getBioObject() {
		return bioObject;
	}

	public void setBioObject(BioObject bioObject) {
		this.bioObject = bioObject;
	}

	/**
	 * Return true if annotation has at least one evidence showing any kind of detection (low, medium, high or positive) else false
	 *
	 * @return an optional boolean or absent if no expression info
	 */
	public Optional<Boolean> isExpressionLevelDetected() {

		Optional<Boolean> isDetected = Optional.empty();

		if (evidences != null) {

			for (AnnotationEvidence evidence : evidences) {

				isDetected = evidence.isExpressionLevelDetected();

				if (isDetected.isPresent() && isDetected.get()) {
					return Optional.of(Boolean.TRUE);
				}
			}
		}

		return isDetected;
	}
	
	public List<String> getSubjectComponents() {
		return subjectComponents;
	}

	public void setSubjectComponents(List<String> subjectComponents) {
		this.subjectComponents = subjectComponents;
	}

	@Deprecated
	public String getAnnotationName() {
		return annotationName;
	}

	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}

	public String getAnnotationHash() {
		return annotationHash;
	}

	public void setAnnotationHash(String annotationHash) {
		this.annotationHash = annotationHash;
	}

	public Set<String> getIndirectEvidenceRefList() {
		return indirectEvidenceRefList;
	}
	
	public synchronized void addIndirectEvidence(String indirectEvidenceId) {
		if(indirectEvidenceRefList == null) {
			indirectEvidenceRefList = new HashSet<String>();
		}
		this.indirectEvidenceRefList.add(indirectEvidenceId);
	}

	public void addRelatedAnnotationName(String annotationUniqueName) {
		if(relatedTo == null) {
			relatedTo = new ArrayList<>();
		}
		relatedTo.add(annotationUniqueName);
	}

	public List<String> getRelatedAnnotationNames() {
		return relatedTo;
	}

}
