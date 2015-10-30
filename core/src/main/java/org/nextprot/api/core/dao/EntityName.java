package org.nextprot.api.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.Overview.EntityNameClass;

public class EntityName implements Serializable, Comparable<EntityName>{

	private static final Log LOGGER = LogFactory.getLog(EntityName.class);

	private static final long serialVersionUID = -6510772648061413417L;
	private Boolean isMain;
	private EntityNameClass clazz;
	private String type;
	private String qualifier;
	private String id;
	private String category;
	private String name;
	private String parentId;
	private List<EntityName> synonyms;
	
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

	public List<EntityName> getSynonyms() {
		return synonyms;
	}
	
	public void setSynonyms(List<EntityName> synonyms) {
		this.synonyms = synonyms;
	}
	
	public void addSynonym(EntityName synonym) {
		if(this.synonyms == null)
			this.synonyms = new ArrayList<EntityName>();
		this.synonyms.add(synonym);
	}

	@Override
	public int compareTo(EntityName o) {
		int thisValue = 10;
		if(this.qualifier != null){
			try {
				thisValue = QualifierValue.valueOf(this.qualifier.replaceAll("\\s+","_").toUpperCase()).ordinal();
			}catch (IllegalArgumentException e){
				e.printStackTrace();
				LOGGER.error("Failed to compare enum values for this qualifier " + this.qualifier + e.getMessage());
			}
		}
		
		int otherValue = 10;
		if(o.qualifier != null){
			try {
				otherValue = QualifierValue.valueOf(o.qualifier.replaceAll("\\s+","_").toUpperCase()).ordinal();
			}catch (IllegalArgumentException e){
				e.printStackTrace();
				LOGGER.error("Failed to compare enum values for other qualifier " + o.qualifier + e.getMessage());
			}
		}

		//orf cases
		if("orf".equalsIgnoreCase(o.category)){ return -1;}
		if("orf".equalsIgnoreCase(this.category)){return 1;}
			
		return thisValue - otherValue;
	}
	
	private static enum QualifierValue {
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

	
}
