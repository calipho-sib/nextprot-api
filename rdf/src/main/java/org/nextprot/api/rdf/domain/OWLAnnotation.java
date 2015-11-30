package org.nextprot.api.rdf.domain;

import org.jsondoc.core.annotation.ApiObject;
import org.nextprot.api.commons.constants.AnnotationCategory;

import java.io.Serializable;
import java.util.Set;

@ApiObject(name = "Annotation", description = "Meta description of an annotation")
public class OWLAnnotation implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;
	private AnnotationCategory cat;

	public OWLAnnotation(AnnotationCategory cat) {
		this.cat=cat;
	}
	
	public OWLAnnotation(String dbAnnotationTypeName) {
		this.cat= AnnotationCategory.getByDbAnnotationTypeName(dbAnnotationTypeName);
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
	public AnnotationCategory getParent() {
		return cat.getParent();
	}
	public Set<AnnotationCategory> getAllParents() {
		return cat.getAllParents();
	}
	public Set<AnnotationCategory> getAllParentsButRoot() {
		return cat.getAllParentsButRoot();
	}
	public String getDescription() {
		return cat.getDescription();
	}

}
