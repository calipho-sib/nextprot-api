package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "peptide", description = "The peptide mapping")
public class PeptideMapping implements Serializable{

	private static final long serialVersionUID = 7304469815021872304L;

	@ApiObjectField(description = "The peptide unique name")
	private String peptideUniqueName;

	@ApiObjectField(description = "The peptide evidences")
	private List<PeptideEvidence> evidences;
	
	@ApiObjectField(description = "The peptide isoform specificity")
	private Map<String, IsoformSpecificity> isoformSpecificity;

	private List<PeptideProperty> properties;
	
	public PeptideMapping() {
		this.isoformSpecificity = new HashMap<String, IsoformSpecificity>();
	}
	
	public String getPeptideUniqueName() {
		return peptideUniqueName;
	}

	public void setPeptideUniqueName(String peptideUniqueName) {
		this.peptideUniqueName = peptideUniqueName;
	}
	
	public void addProperty(PeptideProperty prop) {
		if (this.properties==null) this.properties= new ArrayList<PeptideProperty>();
		this.properties.add(prop);
	}
	
	public void addEvidence(PeptideEvidence evidence) {
		if(this.evidences == null)
			this.evidences = new ArrayList<PeptideMapping.PeptideEvidence>();
		this.evidences.add(evidence);
	}
	
	public List<PeptideEvidence> getEvidences() {
		return this.evidences;
	}
	
	public Map<String, IsoformSpecificity> getIsoformSpecificity() {
		return this.isoformSpecificity;
	}

	public void setIsoformSpecificity(
			Map<String, IsoformSpecificity> isoformSpecificity) {
		this.isoformSpecificity = isoformSpecificity;
	}

	
	public void addIsoformSpecificityOld(IsoformSpecificity newIsoformSpecificity) {
		IsoformSpecificity iso = null;
		
		if(this.isoformSpecificity.containsKey(newIsoformSpecificity.getIsoformName())) { // add position
			iso = this.isoformSpecificity.get(newIsoformSpecificity.getIsoformName());
			iso.addPosition(newIsoformSpecificity.getPositions().get(0));
			this.isoformSpecificity.put(newIsoformSpecificity.getIsoformName(), iso);
		} else this.isoformSpecificity.put(newIsoformSpecificity.getIsoformName(), newIsoformSpecificity);
	}
	
	
	public void addIsoformSpecificity(IsoformSpecificity newIsoformSpecificity) {
		String isoName = newIsoformSpecificity.getIsoformName();
		if(this.isoformSpecificity.containsKey(isoName)) { // add position
			IsoformSpecificity isospec = this.isoformSpecificity.get(isoName);
			isospec.addPosition(newIsoformSpecificity.getPositions().get(0));
		} else {
			this.isoformSpecificity.put(isoName, newIsoformSpecificity);
		}
	}
	
	
	/**
	 * 
	 * @param isoformName a nextprot isoform unique name (starting with NX_)
	 * @return true if the mapping applies to the isoform otherwise false
	 */
	public boolean isSpecificForIsoform(String isoformName) {
		return this.isoformSpecificity.containsKey(isoformName);
	}
	
	

	public List<PeptideProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<PeptideProperty> properties) {
		this.properties = properties;
	}



	public static class PeptideEvidence implements Serializable{

		private static final long serialVersionUID = -6416415250105609274L;
		private String peptideName;
		private String accession;
		private String databaseName;
		private String assignedBy;
		private Long resourceId;
		private String resourceType;
		
		public String getPeptideName() {
			return peptideName;
		}
		public void setPeptideName(String peptideName) {
			this.peptideName = peptideName;
		}
		public String getAccession() {
			
			return accession;
		}
		public void setAccession(String accession) {
			this.accession = accession;
		}
		public String getDatabaseName() {
			return databaseName;
		}
		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}
		public String getAssignedBy() {
			return assignedBy;
		}
		public void setAssignedBy(String assignedBy) {
			this.assignedBy = assignedBy;
		}
		public Long getResourceId() {
			return resourceId;
		}
		public void setResourceId(Long resourceId) {
			this.resourceId = resourceId;
		}
		public String getResourceType() {
			return resourceType;
		}
		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}
	}
	
	
	public static class PeptideProperty implements Serializable {

		private static final long serialVersionUID = 7484965874568857427L;

		private Long peptideId;
		private Long id;
		private Long nameId;
		private String name;
		private String value;
		private String peptideName;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getPeptideId() {
			return peptideId;
		}

		public void setPeptideId(Long peptideId) {
			this.peptideId = peptideId;
		}

		public String getPeptideName() {
			return peptideName;
		}

		public void setPeptideName(String peptideName) {
			this.peptideName = peptideName;
		}

		public Long getNameId() {
			return nameId;
		}

		public void setNameId(Long nameId) {
			this.nameId = nameId;
		}
	}

	
}
