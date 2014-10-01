package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.util.Set;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.commons.constants.AnnotationApiModel;

@ApiObject(name = "Annotation", description = "Meta description of an annotation")
public class OWLAnnotation implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;
	private AnnotationApiModel cat;

	public OWLAnnotation(AnnotationApiModel cat) {
		this.cat=cat;
	}
	
	public OWLAnnotation(String dbAnnotationTypeName) {
		this.cat=AnnotationApiModel.getByDbAnnotationTypeName(dbAnnotationTypeName);
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
	public Set<AnnotationApiModel> getParents() {
		return cat.getParents();
	}
	public Set<AnnotationApiModel> getAllParents() {
		return cat.getAllParents();
	}
	public String getDescription() {
		return cat.getDescription();
	}

}
