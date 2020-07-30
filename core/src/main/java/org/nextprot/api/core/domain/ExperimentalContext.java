package org.nextprot.api.core.domain;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class ExperimentalContext implements Serializable{

	private static final long serialVersionUID = 1821144711310429872L;
	
	// The experimental context identifier
	private Long contextId; // primary key
	
	private CvTerm tissue;
	private CvTerm developmentalStage;
	private CvTerm cellLine;
	private CvTerm disease;
	private CvTerm organelle;
	
	// The experimental context detection method (ECO code)
	private CvTerm detectionMethod;  // ECO code

	//The experimental context metadata document identifier
	private Long metadataId; // publication id = resource id as well
	
	
	
	public Long getMetadataId() {
		return metadataId;
	}
	public void setMetadataId(Long metadataId) {
		this.metadataId = metadataId;
	}
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public CvTerm getTissue() {
		return tissue;
	}
	public String getTissueAC() {
		return (tissue != null) ? tissue.getAccession() : null;
	}
	public void setTissue(CvTerm tissue) {
		this.tissue = tissue;
	}

	public CvTerm getDevelopmentalStage() {
		return developmentalStage;
	}
	public String getDevelopmentalStageAC() {
		return (developmentalStage != null) ? developmentalStage.getAccession() : null;
	}
	public void setDevelopmentalStage(CvTerm developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	public CvTerm getCellLine() {
		return cellLine;
	}
	public String getCellLineAC() {
		return (cellLine != null) ? cellLine.getAccession() : null;
	}

	public void setCellLine(CvTerm cellLine) {
		this.cellLine = cellLine;
	}

	public CvTerm getDisease() {
		return disease;
	}
	public String getDiseaseAC() {
		return (disease != null) ? disease.getAccession() : null;
	}
	public void setDisease(CvTerm disease) {
		this.disease = disease;
	}

	public CvTerm getOrganelle() {
		return organelle;
	}
	public String getOrganelleAC() {
		return (organelle != null) ? organelle.getAccession() : null;
	}

	public void setOrganelle(CvTerm organelle) {
		this.organelle = organelle;
	}

    @JsonIgnore
	public CvTerm getDetectionMethod() {
		return detectionMethod;
	}
    
    @JsonIgnore
	public String getDetectionMethodAC() {
		return (detectionMethod != null) ? detectionMethod.getAccession() : null;
	}
	public void setDetectionMethod(CvTerm detectionMethod) {
		this.detectionMethod = detectionMethod;
	}

	
}
