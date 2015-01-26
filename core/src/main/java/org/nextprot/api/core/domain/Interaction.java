package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "interaction", description = "The interactions")
public class Interaction implements Serializable{

	private static final long serialVersionUID = -4893142951872871844L;

	private Long evidenceId;
	private String evidenceType;

	@ApiObjectField(description = "The datasource of the interaction evidence")
	private String evidenceDatasource;
	private String evidenceQuality;
	private Long evidenceResourceId;
	private String evidenceResourceType;
	private boolean selfInteraction=false;		
	@ApiObjectField(description = "The local db identifier")
	private Long partnershipId;
	@ApiObjectField(description = "The md5 of the interaction as an identifier")
	private String md5;	

	// members below should be read from the db when the field and value are available
	private String evidenceCodeAC="ECO:0000353";
	private String evidenceCodeName="physical interaction evidence used in manual assertion";

	
	public String getEvidenceCodeAC() {
		return evidenceCodeAC;
	}

	public void setEvidenceCodeAC(String evidenceCodeAC) {
		this.evidenceCodeAC = evidenceCodeAC;
	}

	public String getEvidenceCodeName() {
		return evidenceCodeName;
	}

	public void setEvidenceCodeName(String evidenceCodeName) {
		this.evidenceCodeName = evidenceCodeName;
	}

	public boolean isSelfInteraction() {
		return selfInteraction;
	}

	public void setSelfInteraction(boolean selfInteraction) {
		this.selfInteraction = selfInteraction;
	}

	@ApiObjectField(description = "The quality (can be gold or silver)")
	private String quality;

	@ApiObjectField(description = "The interaction database")
	private String evidenceXrefDB;
	
	@ApiObjectField(description = "The accession code of the interaction")
	private String evidenceXrefAC;

	@ApiObjectField(description = "The url of the interaction")
	private String evidenceXrefURL;

	@ApiObjectField(description = "The number of experiments")
	private int numberOfExperiments;
	
	@ApiObjectField(description = "The interactants")
	private List<Interactant> interactants;

	public Interaction() {
		interactants = new ArrayList<Interactant>();
	}
	
	public Long getEvidenceId() {
		return evidenceId;
	}

	public void setEvidenceId(Long evidenceId) {
		this.evidenceId = evidenceId;
	}

	public String getEvidenceType() {
		return evidenceType;
	}

	public void setEvidenceType(String evidenceType) {
		this.evidenceType = evidenceType;
	}

	public String getEvidenceQuality() {
		return evidenceQuality;
	}

	public void setEvidenceQuality(String evidenceQuality) {
		this.evidenceQuality = evidenceQuality;
	}

	public String getEvidenceDatasource() {
		return evidenceDatasource;
	}

	public Long getId() {
		return partnershipId;
	}

	public void setId(Long partnershipId) {
		this.partnershipId = partnershipId;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public void setEvidenceDatasource(String name) {
		this.evidenceDatasource = name;
	}

	public int getNumberOfExperiments() {
		return numberOfExperiments;
	}

	public void setNumberOfExperiments(int numberOfExperiments) {
		this.numberOfExperiments = numberOfExperiments;
	}

	public List<Interactant> getInteractants() {
		return interactants;
	}

	public void setInteractants(List<Interactant> interactants) {
		this.interactants = interactants;
	}

	public void addInteractant(Interactant interactant) {
		this.interactants.add(interactant);
	}
	
	public String getEvidenceXrefDB() {
		return evidenceXrefDB;
	}

	public void setEvidenceXrefDB(String database) {
		this.evidenceXrefDB = database;
	}

	public String getEvidenceXrefAC() {
		return evidenceXrefAC;
	}

	public void setEvidenceXrefAC(String accession) {
		this.evidenceXrefAC = accession;
	}

	public String getEvidenceXrefURL() {
		return evidenceXrefURL;
	}

	public void setEvidenceXrefURL(String url) {
		this.evidenceXrefURL = url;
	}


	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	// according to issue http://issues.isb-sib.ch/browse/CALIPHOMISC-149
	// we always propagate the interactions to all isoforms but we set the specificity property
	public boolean isInteractionValidForIsoform(String isoform) {
		return true;
	}

	/**
	 * Determines if an interaction is specific to the isoform passed as the parameter
	 * @param isoform the name of an isoform
	 * @return true if the parameter matches the name of the isoform of the entry point
	 */
	public boolean isInteractionSpecificForIsoform(String isoform) {
		boolean result = false;
		for (Interactant act: interactants) {
			if (act.isEntryPoint() && act.isNextprot() && act.isIsoform()) {
				if (isoform.startsWith("NX_")) isoform = isoform.substring(3);
				if (act.getAccession().equals(isoform)) result = true;
			}
		}
		return result;
	}

	public Long getEvidenceResourceId() {
		return evidenceResourceId;
	}

	public void setEvidenceResourceId(Long evidenceResourceId) {
		this.evidenceResourceId = evidenceResourceId;
	}

	public String getEvidenceResourceType() {
		return evidenceResourceType;
	}

	public void setEvidenceResourceType(String evidenceResourceType) {
		this.evidenceResourceType = evidenceResourceType;
	}

}
