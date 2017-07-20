package org.nextprot.api.core.domain;

import org.nextprot.api.commons.constants.TerminologyMapping;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.utils.TerminologyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CvTerm implements Serializable {
    
	private static final long serialVersionUID = 4404147147281845675L;

	private Long id;
	private String accession;
	private String name;
	private String description;
	private String ontology;
	private String ontologyAltname;
	//private List<String> sameAs = new ArrayList<>();

	private List<String> parentAccession;
	private List<String> childAccession;
	private List<DbXref> xrefs;
	private List<String> synonyms;
	private List<TermProperty> properties;

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

	public void setSynonyms(String synonyms) {
		if (synonyms == null)
			return;
		List<String> allsyn = Arrays.asList(synonyms.split("\\|"));
		this.synonyms = allsyn;
	}

	public List<TermProperty> getProperties() {
		return properties;
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
		return childAccession;
	}

	public void setChildAccession(String accession) {
		if (accession == null)
			return;
		List<String> all = Arrays.asList(accession.split("\\|"));
		this.childAccession = all;

	}

	public List<String> getAncestorAccession() {
		return parentAccession;
	}

	public void setAncestorAccession(String accession) {
		if (accession == null)
			return;
		List<String> all = Arrays.asList(accession.split("\\|"));
		this.parentAccession = all;
	}

	public List<DbXref> getXrefs() {
		return xrefs;
	}

	public List<DbXref> getFilteredXrefs(String category) {
		if(xrefs == null) return null;
		List<DbXref> filteredxrefs = new ArrayList<>();
		for (DbXref currxref : xrefs) {
			if(currxref.getDatabaseCategory().equals(category)) filteredxrefs.add(currxref);
		}
		if(filteredxrefs.size() == 0) return null;
		return filteredxrefs;
	}

	public void setXrefs(List<DbXref> xrefs) {
		this.xrefs = xrefs;
	}

	public List<String> getSameAs() {
		// To remain compatible with previous API version (Terminology.getSameAs() is used for ttl generation in term.ttl.vm )
		return TerminologyUtils.convertXrefsToSameAsStrings(getFilteredXrefs("Ontologies"));
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
		sb.append(this.getSameAs());
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
		sb.append("ancestors=");
		sb.append(this.parentAccession);
		sb.append("\n");
		
		return sb.toString();
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
	
}


