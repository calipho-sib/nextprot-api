package org.nextprot.rdf.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Annotation", description = "Meta description of an annotation")
public class OWLAnnotation implements Serializable{

	private static final long serialVersionUID = 4404147147281845675L;
	

	private String type;
	
	private String parent;

	private String description;

	private static List<String> done=new ArrayList<String>();


	public void clearPredicatList(){
		done.clear();		
	}
	
	public String getPredicat(){
		done.add(OWLAnnotationCategory.getByType(type).getPredicat());
		return OWLAnnotationCategory.getByType(type).getPredicat();
	}

	public String getLabel(){
		return OWLAnnotationCategory.getByType(type).getLabel();
	}
	
	public String getParent() {
		return parent;
	}
	
	public List<String> getDomain(){
		return OWLAnnotationCategory.getByType(type).getDomain();
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFirstTimePredicat(){
		return !done.contains(OWLAnnotationCategory.getByType(type).getPredicat());
	}
}
