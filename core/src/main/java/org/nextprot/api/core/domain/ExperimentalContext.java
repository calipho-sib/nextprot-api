package org.nextprot.api.core.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import java.io.Serializable;

@ApiObject(name = "experimentalContext", description = "The experimental context")
public class ExperimentalContext implements Serializable{

	private static final long serialVersionUID = 1821144711310429872L;
	
	@ApiObjectField(description = "The experimental context identifier")
	private Long contextId; // primary key
	
	private Terminology tissue;
	private Terminology developmentalStage;
	private Terminology cellLine;
	private Terminology disease;
	private Terminology organelle;
	@ApiObjectField(description = "The experimental context detection method (ECO code)")
	private Terminology detectionMethod;  // ECO code

	@ApiObjectField(description = "The experimental context metadata document identifier (md5)")
	private String metadataAC;  // md5 of publication
	
	
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public Terminology getTissue() {
		return tissue;
	}
	public String getTissueAC() {
		return (tissue != null) ? tissue.getAccession() : null;
	}
	public void setTissue(Terminology tissue) {
		this.tissue = tissue;
	}

	public Terminology getDevelopmentalStage() {
		return developmentalStage;
	}
	public String getDevelopmentalStageAC() {
		return (developmentalStage != null) ? developmentalStage.getAccession() : null;
	}
	public void setDevelopmentalStage(Terminology developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	public Terminology getCellLine() {
		return cellLine;
	}
	public String getCellLineAC() {
		return (cellLine != null) ? cellLine.getAccession() : null;
	}

	public void setCellLine(Terminology cellLine) {
		this.cellLine = cellLine;
	}

	public Terminology getDisease() {
		return disease;
	}
	public String getDiseaseAC() {
		return (disease != null) ? disease.getAccession() : null;
	}
	public void setDisease(Terminology disease) {
		this.disease = disease;
	}

	public Terminology getOrganelle() {
		return organelle;
	}
	public String getOrganelleAC() {
		return (organelle != null) ? organelle.getAccession() : null;
	}

	public void setOrganelle(Terminology organelle) {
		this.organelle = organelle;
	}

	public Terminology getDetectionMethod() {
		return detectionMethod;
	}
	public String getDetectionMethodAC() {
		return (detectionMethod != null) ? detectionMethod.getAccession() : null;
	}
	public void setDetectionMethod(Terminology detectionMethod) {
		this.detectionMethod = detectionMethod;
	}
	public String getMetadataAC() {
		return metadataAC;
	}
	public void setMetadataAC(String ac) {
		this.metadataAC = ac;
	}

	
}
