package org.nextprot.api.core.domain;

import java.util.List;

import org.nextprot.api.core.domain.annotation.IsoformAnnotation;

public class ModifiedEntry { //Sous class de Entry.java ?

	//public Entry parent; ?
	public String subjectName;
	public List<IsoformAnnotation> subjectComponents; // For example subject components 
	public List<IsoformAnnotation> annotations;
	
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public List<IsoformAnnotation> getSubjectComponents() {
		return subjectComponents;
	}
	public void setSubjectComponents(List<IsoformAnnotation> subjectComponents) {
		this.subjectComponents = subjectComponents;
	}
	public List<IsoformAnnotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<IsoformAnnotation> annotations) {
		this.annotations = annotations;
	}

}
