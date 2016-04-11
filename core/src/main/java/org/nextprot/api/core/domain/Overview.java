package org.nextprot.api.core.domain;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.dao.EntityName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Overview implements Serializable{

	private static final long serialVersionUID = 2L;

	private static class PE {
		String name;
		int level;
		PE(String name, int level) {this.name=name; this.level=level;}
	}
	private static Map<String,PE> peMap;
    static {
    	// key: as stored in db, value: as to be displayed
    	peMap = new HashMap<>();
    	peMap.put("protein level", new PE("Evidence_at_protein_level",1));
    	peMap.put("transcript level", new PE("Evidence_at_transcript_level",2));
    	peMap.put("homology", new PE("Inferred_from_homology",3));
    	peMap.put("predicted", new PE("Predicted",4));
    	peMap.put("uncertain", new PE("Uncertain",5));
    }	
	private History history;
	private List<Family> families;
	private List<EntityName> proteinNames;
	private List<EntityName> geneNames;
	private List<EntityName> functionalRegionNames;
	private List<EntityName> cleavedRegionNames;
	private List<EntityName> additionalNames;
	private List<EntityName> isoformNames;
	
	public List<EntityName> getIsoformNames() {
		return isoformNames;
	}

	public void setIsoformNames(List<EntityName> isoformNames) {
		this.isoformNames = isoformNames;
	}

	public History getHistory() {
		return history;
	}
	
	/**
	 * The recommended name is composed by 1 full name and can optionally contain n short names and n ECs (enzyme names)
	 * @return the recommended name as full and its synonyms (shorts ent ECs) if they exists
	 */
	public EntityName getRecommendedProteinName() {
		EntityName recommendedName = new EntityName();
		for(EntityName name : this.proteinNames){
			if(name.isMain()){
				recommendedName.setCategory(name.getCategory());
				recommendedName.setClazz(name.getClazz());
				recommendedName.setId(name.getId());
				recommendedName.setMain(true);
				recommendedName.setName(name.getName());
				recommendedName.setParentId(name.getParentId());
				recommendedName.setQualifier(name.getQualifier());
				recommendedName.setType(name.getType());
				for(EntityName sname : name.getSynonyms()){
					if(!sname.getQualifier().equals("full")){
						recommendedName.addSynonym(sname); //add the short and children
					}
				}
				for(EntityName sname : name.getOtherRecommendedEntityNames()){
					if(!sname.getQualifier().equals("full")){
						recommendedName.addOtherRecommendedEntityName(sname); //add the short and children
					}
				}
			}
		}
		return recommendedName;
	}
	
	/**
	 * Each alternative name can either 1 full name with n shorts and n ECs. 
	 * We also include here the additional names: allergen / CD antigen and INN 
	 * @return
	 */
	public List<EntityName> getAlternativeProteinNames() {
		List<EntityName> result = new ArrayList<>();
		for(EntityName name : this.proteinNames){
			if(name.isMain()){
				if(name.getSynonyms() != null){
					for(EntityName sname : name.getSynonyms()){
						if(sname.getQualifier().equals("full")){
							result.add(sname); 
						}
					}
				}
			}
		}

		//adding additional names into alternatives
		if(this.additionalNames != null){ //this includes CD antigen / allergen and INN
			result.addAll(this.additionalNames); 
		}
		
		Collections.sort(result);
		return result;
	}

		
	public String getProteinExistenceInfo() {
		return this.history.getProteinExistenceInfo();
	}
	
	public String getProteinExistence() {
		return this.history.getProteinExistence();
	}
	
	public int getProteinExistenceLevel() {
		return this.history.getProteinExistenceLevel();
	}


	public void setHistory(History history) {
		this.history = history;
	}


	public List<Family> getFamilies() {
		return families;
	}

	public void setFamilies(List<Family> families) {
		this.families = families;
	}

	public static class History implements Serializable {

		private static final long serialVersionUID = 778801504825937620L;

		@Deprecated
		private String proteinExistence, proteinExistenceInfo;
		private Date nextprotIntegrationDate;
		private Date nextprotUpdateDate;
		private Date uniprotIntegrationDate;
		private Date uniprotUpdateDate;
		private String uniprotVersion;
		private Date lastSequenceUpdate;
		private String sequenceVersion;

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		/**
		 * 
		 * @return a string to be displayed in ttl, xml, etc. representing the protein existence
		 */
		@Deprecated //Should use overview instead
		public String getProteinExistence() {
			return Overview.peMap.get(proteinExistence).name;
		}

		@Deprecated //Should use overview instead
		public String getProteinExistenceInfo() {
			return proteinExistenceInfo;
		}

		/**
		 * 
		 * @return the string stored in the db (not the one to be displayed, experted, etc...)
		 */
		@Deprecated //Should use overview instead
		public String getProteinExistenceRaw() {
			return this.proteinExistence;
		}

		/**
		 * 
		 * @return an integer representing the protein existence level between 1 and 5. 
		 * 1 is the highest level of certainty
		 */
		@Deprecated //Should use overview instead
		public int getProteinExistenceLevel() {
			return Overview.peMap.get(proteinExistence).level;
		}

		@Deprecated //Should use overview instead
		public void setProteinExistenceInfo(String proteinExistenceInfo) {
			this.proteinExistenceInfo = proteinExistenceInfo;
		}

		/**
		 * 
		 * @param proteinExistence string as stored in the sequence identifier property value 
		 */
		public void setProteinExistence(String proteinExistence) {
			this.proteinExistence = proteinExistence;
		}

		public Date getNextprotIntegrationDate() {
			return nextprotIntegrationDate;
		}
		
		public String getFormattedNextprotIntegrationDate() {
			return this.dateFormat.format(this.nextprotIntegrationDate);
		}

		public void setNextprotIntegrationDate(Date nextprotIntegrationDate) {
			this.nextprotIntegrationDate = nextprotIntegrationDate;
		}

		public Date getNextprotUpdateDate() {
			return nextprotUpdateDate;
		}

		public String getFormattedNextprotUpdateDate() {
			return this.dateFormat.format(this.nextprotUpdateDate);
		}
		
		public void setNextprotUpdateDate(Date nextprotUpdateDate) {
			this.nextprotUpdateDate = nextprotUpdateDate;
		}

		public Date getUniprotIntegrationDate() {
			return uniprotIntegrationDate;
		}

		public String getFormattedUniprotIntegrationDate() {
			return this.dateFormat.format(this.uniprotIntegrationDate);
		}
		
		public void setUniprotIntegrationDate(Date uniprotIntegrationDate) {
			this.uniprotIntegrationDate = uniprotIntegrationDate;
		}

		public Date getUniprotUpdateDate() {
			return uniprotUpdateDate;
		}
		
		public String getFormattedUniprotUpdateDate() {
			return this.dateFormat.format(this.uniprotUpdateDate);
		}

		public void setUniprotUpdateDate(Date uniprotUpdateDate) {
			this.uniprotUpdateDate = uniprotUpdateDate;
		}

		public String getUniprotVersion() {
			return uniprotVersion;
		}

		public void setUniprotVersion(String uniprotVersion) {
			this.uniprotVersion = uniprotVersion;
		}

		public Date getLastSequenceUpdate() {
			return lastSequenceUpdate;
		}

		public void setLastSequenceUpdate(Date lastSequenceUpdate) {
			this.lastSequenceUpdate = lastSequenceUpdate;
		}

		public String getSequenceVersion() {
			return sequenceVersion;
		}

		public void setSequenceVersion(String sequenceVersion) {
			this.sequenceVersion = sequenceVersion;
		}
	}
	
	
	public static enum EntityNameClass {
		PROTEIN_NAMES("proteinNames"),
		GENE_NAMES("geneNames"),
		FUNCTIONAL_REGION_NAMES("functionalRegionNames"),
		CLEAVED_REGION_NAMES("cleavedRegionNames"),
		ADDITIONAL_NAMES("additionalNames"); //TODO not sure if we need additional names in the API anymore
		
		private String className;
		
		EntityNameClass(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
		
		public static EntityNameClass getValue(String value) {
			for(EntityNameClass clazz : EntityNameClass.values())
				if(clazz.className.equals(value)) return clazz;
			return null;
		}
	}

	public List<EntityName> getProteinNames() {
		return proteinNames;
	}

	public String getMainProteinName() {

		EntityName name = getMainEntityName(proteinNames, EntityNameClass.PROTEIN_NAMES);

		return name.getName();
	}

	public void setProteinNames(List<EntityName> proteinNames) {
		this.proteinNames = proteinNames;
	}

	/**
	 * Contains gene names and ORF names
	 * @return
	 */
	public List<EntityName> getGeneNames() {
		return geneNames;
	}

	public String getMainGeneName() {

		EntityName name = getMainEntityName(geneNames, EntityNameClass.GENE_NAMES);
        if(name != null) return name.getName();
        else return null;
	}

	public boolean hasMainGeneName() {

		return geneNames != null && getMainEntityName(geneNames, EntityNameClass.GENE_NAMES) != null;
	}

	public boolean hasMainProteinName() {

		return proteinNames != null && getMainEntityName(proteinNames, EntityNameClass.PROTEIN_NAMES) != null;
	}

	public void setGeneNames(List<EntityName> geneNames) {
		this.geneNames = geneNames;
	}

	public List<EntityName> getFunctionalRegionNames() {
		return functionalRegionNames;
	}

	public void setFunctionalRegionNames(List<EntityName> functionalRegionNames) {
		this.functionalRegionNames = functionalRegionNames;
	}

	public List<EntityName> getCleavedRegionNames() {
		return cleavedRegionNames;
	}

	public void setCleavedRegionNames(List<EntityName> cleavedRegionNames) {
		this.cleavedRegionNames = cleavedRegionNames;
	}

	public List<EntityName> getAdditionalNames() {
		return additionalNames;
	}

	public void setAdditionalNames(List<EntityName> additionalNames) {
		this.additionalNames = additionalNames;
	}

	/**
	 * Get the main entity name
	 * @param entityNameList the list of entity names
	 * @return
	 */
	private static EntityName getMainEntityName(List<EntityName> entityNameList, EntityNameClass entityNameClass) {

		if (entityNameList != null) {
			for (EntityName entityName : entityNameList) {

				if (entityName.isMain()) return entityName;
			}
		}
		
		if(entityNameClass.equals(EntityNameClass.PROTEIN_NAMES)){
			throw new NextProtException("could not find main  protein name");
		}else return null;

	}
}
