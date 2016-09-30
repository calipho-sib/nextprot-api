package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.List;

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
	
	protected abstract boolean filterOutAnnotation(Annotation a);
	protected abstract boolean filterOutXref(DbXref x);

	public boolean hasContent(Entry entry) {

		return true;
	}

	public String getPageName() {

		return this.getClass().getSimpleName();
	}
}
