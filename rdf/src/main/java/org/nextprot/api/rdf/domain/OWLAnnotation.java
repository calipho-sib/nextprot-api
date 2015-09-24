package org.nextprot.api.rdf.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.commons.constants.AnnotationApiModel;

import java.io.Serializable;
import java.util.Set;

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
	@Deprecated
	public String getRdfTypeName(){
		return cat.getApiTypeName();
	}
	public String getApiTypeName(){
		return cat.getApiTypeName();
	}
	public AnnotationApiModel getParent() {
		return cat.getParent();
	}
	public Set<AnnotationApiModel> getAllParents() {
		return cat.getAllParents();
	}
	public Set<AnnotationApiModel> getAllParentsButRoot() {
		return cat.getAllParentsButRoot();
	}
	public String getDescription() {
		return cat.getDescription();
	}

}
