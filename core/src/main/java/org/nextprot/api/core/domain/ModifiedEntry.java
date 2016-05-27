package org.nextprot.api.core.domain;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ModifiedEntry { //Sous class de Entry.java ?

	//public Entry parent; ?
	public String subjectName;
	public List<Annotation> subjectComponents; // For example subject components 
	public List<Annotation> annotations;

}
