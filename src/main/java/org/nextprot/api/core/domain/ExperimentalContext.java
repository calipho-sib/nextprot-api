package org.nextprot.api.core.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "experimentalContext", description = "The experimental context")
public class ExperimentalContext implements Serializable{

	private static final long serialVersionUID = 1821144711310429872L;
	
	@ApiObjectField(description = "The experimental context identifier")
	private Long contextId; // primary key
	
	private String tissueAC;
	private String developmentalStageAC;
	private String cellLineAC;
	private String diseaseAC;
	private String organelleAC;

	@ApiObjectField(description = "The experimental context detection method (ECO code)")
	private String detectionMethodAC;  // ECO code

	@ApiObjectField(description = "The experimental context metadata document identifier (md5)")
	private String metadataAC;  // md5 of publication
	
	
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public String getTissueAC() {
		return tissueAC;
	}
	public void setTissueAC(String tissueAC) {
		this.tissueAC = tissueAC;
	}
	public String getDevelopmentalStageAC() {
		return developmentalStageAC;
	}
	public void setDevelopmentalStageAC(String developmentalStageAC) {
		this.developmentalStageAC = developmentalStageAC;
	}
	public String getCellLineAC() {
		return cellLineAC;
	}
	public void setCellLineAC(String cellLineAC) {
		this.cellLineAC = cellLineAC;
	}
	public String getDiseaseAC() {
		return diseaseAC;
	}
	public void setDiseaseAC(String diseaseAC) {
		this.diseaseAC = diseaseAC;
	}
	public String getOrganelleAC() {
		return organelleAC;
	}
	public void setOrganelleAC(String organelleAC) {
		this.organelleAC = organelleAC;
	}
	public String getDetectionMethodAC() {
		return detectionMethodAC;
	}
	public void setDetectionMethodAC(String detectionMethodAC) {
		this.detectionMethodAC = detectionMethodAC;
	}
	public String getMetadataAC() {
		return metadataAC;
	}
	public void setMetadataAC(String ac) {
		this.metadataAC = ac;
	}

	
}
