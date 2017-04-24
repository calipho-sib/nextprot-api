package org.nextprot.api.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Overview.EntityNameClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityName implements Serializable {

	private static final Log LOGGER = LogFactory.getLog(EntityName.class);

	private static final long serialVersionUID = 3L;
	private Boolean isMain;
	private EntityNameClass clazz;
	private String type;
	private String qualifier;
	private String id;
	private String category;
	private String name;
	private String parentId;
	
	//TODO Is this the same as saying parentName??? Is this really needed for isoforms????
	private String mainEntityName;

	private List<EntityName> recommendedEntityNames = new ArrayList<>();
	private List<EntityName> synonyms = new ArrayList<>();
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public Boolean isMain() {
		return isMain;
	}
	
	public void setMain(Boolean isMain) {
		this.isMain = isMain;
	}
	
	public EntityNameClass getClazz() {
		return clazz;
	}

	public void setClazz(EntityNameClass clazz) {
		this.clazz = clazz;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getQualifier() {
		return qualifier;
	}
	
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String synonymId) {
		this.id = synonymId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<EntityName> getOtherRecommendedEntityNames() { return recommendedEntityNames; }

	public void addOtherRecommendedEntityName(EntityName recommendedName) {
		this.recommendedEntityNames.add(recommendedName);
	}

	public List<EntityName> getSynonyms() {
		return synonyms;
	}

	public void addSynonym(EntityName synonym) {
		this.synonyms.add(synonym);
	}

	public void addAllSynonyms(List<EntityName> synonyms) {
		this.synonyms.addAll(synonyms);
	}

	// defined for EntityNameClass.PROTEIN_NAMES, EntityNameClass.FUNCTIONAL_REGION_NAMES and EntityNameClass.CLEAVED_REGION_NAMES
	private enum QualifierValue {
		FULL, SHORT, EC, ALLERGEN, CD_ANTIGEN, INN 
	}

	public String getComposedName(){
		String qualifier="", type=getType();
		if(getQualifier()!=null){
			qualifier=getQualifier();
//			return qualifier+type.substring(0, 1).toUpperCase() + type.substring(1);
			return qualifier+" "+type;
		}
		return getType();
	}

	@Deprecated //Fix javascript client and remove this method
	public String getValue() {
		return name;
	}
	@Deprecated //Fix javascript client and remove this method
	public void setValue(String value) {
		this.name = value;
	}

	//TODO Is this the same as saying parentName??? Is this really needed for isoforms????
	public void setMainEntityName(String mainEntityName) {
		this.mainEntityName = mainEntityName;
	}

	//TODO Is this the same as saying parentName??? Is this really needed for isoforms????
	public String getMainEntityName() {
		return this.mainEntityName;
	}

	public static String toString(List<EntityName> entityNameList) {

		StringBuilder sb = new StringBuilder();

		if (entityNameList != null) {

			for (EntityName entityName : entityNameList) {

				sb.append(entityName.getName());
				sb.append(", ");
			}

			if (sb.length() > 0) {
				sb.delete(sb.length() - 2, sb.length());
			}
		}

		return sb.toString();
	}

	public static Comparator<EntityName> newDefaultComparator() {

		return new EntityName.ByCategoryComparator()
				.thenComparing(new EntityName.ByQualifierValueComparator())
				.thenComparing(EntityName::getName);
	}

	public static class ByQualifierValueComparator implements Comparator<EntityName> {

		@Override
		public int compare(EntityName en1, EntityName en2) {

			if (en1.qualifier != null && en2.qualifier != null) {
				try {
					QualifierValue qv1 = EntityName.QualifierValue.valueOf(en1.qualifier.replaceAll("\\s+","_").toUpperCase());
					QualifierValue qv2 = EntityName.QualifierValue.valueOf(en2.qualifier.replaceAll("\\s+","_").toUpperCase());

					return qv1.ordinal() - qv2.ordinal();
				} catch (IllegalArgumentException e) {
					throw new NextProtException("Failed to compare enum values " + en1.qualifier + " vs "+ en2.qualifier + ": " + e.getMessage());
				}
			}

			return 0;
		}
	}

	public static class ByCategoryComparator implements Comparator<EntityName> {

		@Override
		public int compare(EntityName en1, EntityName en2) {

			// 1. ORFs come last
			if("orf".equalsIgnoreCase(en1.getCategory())) {
				return 1;
			}

			if("orf".equalsIgnoreCase(en2.getCategory())) {
				return -1;
			}

			return 0;
		}
	}
}
