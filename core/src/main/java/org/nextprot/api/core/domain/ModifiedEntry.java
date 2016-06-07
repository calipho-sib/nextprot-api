package org.nextprot.api.core.domain;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ModifiedEntry { //Sous class de Entry.java ?

	//public Entry parent; ?
	public String subjectName;
	public List<Annotation> subjectComponents; // For example subject components 
	public List<Annotation> annotations;
	
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public List<Annotation> getSubjectComponents() {
		return subjectComponents;
	}
	public void setSubjectComponents(List<Annotation> subjectComponents) {
		this.subjectComponents = subjectComponents;
	}
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

}
