package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Overview implements Serializable{

	private static final long serialVersionUID = 4L;

	private History history;
	private List<Family> families;
	private List<EntityName> proteinNames;
	private List<EntityName> geneNames;
	private List<EntityName> functionalRegionNames;
	private List<EntityName> cleavedRegionNames;
	private List<EntityName> additionalNames;
	private List<EntityName> isoformNames;
	private ProteinExistences proteinExistences;
	
	public List<EntityName> getIsoformNames() {
		return isoformNames;
	}

	public void setIsoformNames(List<EntityName> isoformNames) {
		this.isoformNames = isoformNames;
	}

	public History getHistory() {
		return history;
	}
	
	public EntityName findGeneEntityName(String recName) {
		for (EntityName e: this.getGeneNames()) {
			if (e.getName().equals(recName)) return e;
		}
		return null;
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
					if(!"full".equals(sname.getQualifier())){
						recommendedName.addSynonym(sname); //add the short and children
					}
				}
				for(EntityName sname : name.getOtherRecommendedEntityNames()){
					if(!"full".equals(sname.getQualifier())){
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
			if(name.isMain() && name.getSynonyms() != null){
				for(EntityName sname : name.getSynonyms()){
					if("full".equals(sname.getQualifier())){
						result.add(sname);
					}
				}
			}
		}

		//adding additional names into alternatives
		if(this.additionalNames != null){ //this includes CD antigen / allergen and INN
			result.addAll(this.additionalNames); 
		}
		
		result.sort(EntityName.newDefaultComparator());
		return result;
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

		private static final long serialVersionUID = 2L;

		private ProteinExistence proteinExistenceUniprot;
		private Date nextprotIntegrationDate;
		private Date nextprotUpdateDate;
		private Date uniprotIntegrationDate;
		private Date uniprotUpdateDate;
		private String uniprotVersion;
		private Date lastSequenceUpdate;
		private String sequenceVersion;

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        public ProteinExistence getProteinExistenceUniprot() {
            return proteinExistenceUniprot;
        }

        public void setProteinExistenceUniprot(ProteinExistence proteinExistenceUniprot) {
            this.proteinExistenceUniprot = proteinExistenceUniprot;
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
	
	
	public enum EntityNameClass {
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

	public ProteinExistence getProteinExistence() {
		return proteinExistences.getProteinExistence();
	}

	@JsonIgnore
	public ProteinExistences getProteinExistences() {
		return proteinExistences;
	}

	public void setProteinExistences(ProteinExistences proteinExistences) {
		this.proteinExistences = proteinExistences;
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
