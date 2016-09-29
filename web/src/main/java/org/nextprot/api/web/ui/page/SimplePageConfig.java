package org.nextprot.api.web.ui.page;

import java.util.List;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

public abstract class SimplePageConfig {
	
	protected List<AnnotationCategory> annotations;
	protected List<AnnotationCategory> features;
	protected List<String> xrefs;
	
	public List<AnnotationCategory> getAnnotations() {
		return annotations;
	}
	public List<AnnotationCategory> getFeatures() {
		return features;
	}
	public List<String> getXrefs() {
		return xrefs;
	}
	
	public abstract boolean filterOutAnnotation(Annotation a);
	public abstract boolean filterOutXref(DbXref x);
	
	
	
}
