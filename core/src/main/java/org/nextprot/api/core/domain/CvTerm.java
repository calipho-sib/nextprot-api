package org.nextprot.api.core.domain;

import org.nextprot.api.commons.constants.TerminologyMapping;
import org.nextprot.api.commons.utils.StreamUtils;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.utils.TerminologyUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CvTerm implements Serializable {


	private static final long serialVersionUID = 4404147147281845675L;

	private Long id;
	private String accession;
	private String name;
	private String description;
	private String ontology;
	private String ontologyAltname;
	private String ontologyDisplayName;
	private List<TermAccessionRelation> ancestorsRelations;
	private List<TermAccessionRelation> childrenRelations;

	private DbXref selfXref;
	private List<DbXref> xrefs;
	private List<String> synonyms;
	private List<TermProperty> properties;
	
	public DbXref getSelfXref() {
		return selfXref;
	}

	public void setSelfXref(DbXref selfXref) {
		this.selfXref = selfXref;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String>  getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public List<TermProperty> getProperties() {
		return properties;
	}

    /**
     * Get the property value
     * @param propertyName the property name
     * @return a value or empty if not found
     */
    public Optional<TermProperty> getProperty(String propertyName) {

	    return properties.stream()
                .filter(p -> p.getPropertyName().equals(propertyName))
                .findFirst();
    }

	public void setProperties(List<TermProperty> properties) {
		this.properties = properties;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOntology() {
		String o = StringUtils.toCamelCase(ontology, false);
		try {
			if(o != null) {
				return TerminologyMapping.valueOf(o).getDescription();
			}else return ontology;
		} catch (IllegalArgumentException e) {
			return ontology;
		}
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getOntologyAltname() {
			return ontologyAltname;
	}

	public void setOntologyAltname(String ontologyAltname) {
		this.ontologyAltname = ontologyAltname;
	}


	public List<String> getChildAccession() {
		if(childrenRelations != null){
			return this.childrenRelations.stream().map(c -> c.getTermAccession()).collect(Collectors.toList());
		}return null;
	}

	public List<String> getAncestorAccession() {
		if(ancestorsRelations != null) {
			return this.ancestorsRelations.stream().map(c -> c.getTermAccession()).collect(Collectors.toList());
		}return null;
	}

	public List<DbXref> getXrefs() {
		return xrefs;
	}

	public void setXrefs(List<DbXref> xrefs) {
		this.xrefs = xrefs;
	}

	private boolean isExternalReference(DbXref x) {
		return x.getPropertyByName("term_id")==null;
	}
	private boolean isRelatedTerm(DbXref x) {
		return ! isExternalReference(x);
	}
	
	/*
	 * Related terms are retrieved from the term xrefs.
	 * It is the subset of xrefs of terms that are actually loaded in neXtProt
	 */
	public List<String> getACsOfRelatedTerms() {
		return StreamUtils.nullableListToStream(this.getXrefs())
			.filter(x -> isRelatedTerm(x))
			.map(x -> x.getAccession())
			.collect(Collectors.toList());
	}



	public List<TermAccessionRelation> getAncestorsRelations() {
		return ancestorsRelations;
	}

	public void setAncestorsRelations(List<TermAccessionRelation> ancestorsRelations) {
		this.ancestorsRelations = ancestorsRelations;
	}

	public List<TermAccessionRelation> getChildrenRelations() {
		return childrenRelations;
	}

	public void setChildrenRelations(List<TermAccessionRelation> childrenRelations) {
		this.childrenRelations = childrenRelations;
	}


	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("id=");
		sb.append(this.id);
		sb.append("\n");
		sb.append("accession=");
		sb.append(this.accession);
		sb.append("\n");
		sb.append("name=");
		sb.append(this.name);
		sb.append("\n");
		sb.append("description=");
		sb.append(this.description);
		sb.append("\n");
		sb.append("synonyms=");
		sb.append(this.synonyms);
		sb.append("\n");
		sb.append("xrefs=");
		sb.append(TerminologyUtils.convertXrefsToString(this.getXrefs()));
		sb.append("\n");
		sb.append("sameAs=");
		sb.append(this.getACsOfRelatedTerms());
		sb.append("\n");
		sb.append("properties=");
		sb.append(TerminologyUtils.convertPropertiesToString(this.getProperties()));
		sb.append("\n");
		sb.append("ontology=");
		sb.append(this.ontology);
		sb.append("\n");
		sb.append("ontologyAltname=");
		sb.append(this.ontologyAltname);
		sb.append("\n");
		sb.append("ontologyDisplayName=");
		sb.append(this.ontologyDisplayName);
		sb.append("\n");
		sb.append("ancestors=");
		sb.append(this.ancestorsRelations);
		sb.append("\n");
		
		return sb.toString();
	}


	public String getOntologyDisplayName() {
		return ontologyDisplayName;
	}
	public void setOntologyDisplayName(String ontologyDisplayName) {
        this.ontologyDisplayName = ontologyDisplayName;
    }

    public static class TermProperty implements Serializable {

		private static final long serialVersionUID = 5662052927182501529L;
		
		private long termId;
		private String propertyName;
		private String propertyValue;
		
		public long gettermId() {
			return termId;
		}
		public void settermId(long termId) {
			this.termId = termId;
		}
		public String getPropertyName() {
			return propertyName;
		}
		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}
		public String getPropertyValue() {
			return propertyValue;
		}
		public void setPropertyValue(String propertyValue) {
			this.propertyValue = propertyValue;
		}

	}



	//Utility class to add relation type to a ancestor / children term
	public static class TermAccessionRelation implements Serializable {

		private String termAccession;
		private String relationType;

		public TermAccessionRelation(String termAccession, String relationType) {
			this.termAccession = termAccession;
			this.relationType = relationType;
		}

		public String getTermAccession() {
			return termAccession;
		}

		public String getRelationType() {
			return relationType;
		}

		@Override
		public String toString(){
			return termAccession + "->" + relationType;
		}

	}

}


