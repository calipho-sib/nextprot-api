package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.util.Set;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.core.domain.OWLAnnotationCategory;

@ApiObject(name = "Annotation", description = "Meta description of an annotation")
public class OWLAnnotation implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;
	private OWLAnnotationCategory cat;

	public OWLAnnotation(OWLAnnotationCategory cat) {
		this.cat=cat;
	}
	
	public OWLAnnotation(String dbAnnotationTypeName) {
		this.cat=OWLAnnotationCategory.getByDbAnnotationTypeName(dbAnnotationTypeName);
	}
	
	public Integer getDbId () {
		return cat.getDbId();
	}	
	public String getPredicate(){
		return cat.getRdfPredicate();
	}
	public String getLabel(){
		return cat.getRdfLabel();
	}
	public String getRdfTypeName(){
		return cat.getRdfTypeName();
	}
	public Set<OWLAnnotationCategory> getParents() {
		return cat.getParents();
	}
	public Set<OWLAnnotationCategory> getAllParents() {
		return cat.getAllParents();
	}
	public String getDescription() {
		return cat.getDescription();
	}

}
