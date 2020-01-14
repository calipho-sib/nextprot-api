package org.nextprot.api.core.service.annotation;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;

public class PeptideSet {
	
	private String name;
	private List<Annotation> annotations = new ArrayList<>();

	public PeptideSet(String name) {
		this.name = name;
	}
	
	public void addAnnotation(Annotation a) {
		this.annotations.add(a);
	}
	
	public List<Annotation> getAnnotations() {
		return this.annotations;
	}
	
	public String getName() {
		return this.name;
	}
	
}
