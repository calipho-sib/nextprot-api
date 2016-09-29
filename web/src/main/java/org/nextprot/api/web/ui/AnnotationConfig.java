package org.nextprot.api.web.ui;

import org.nextprot.api.commons.constants.AnnotationCategory;

public class AnnotationConfig {

	AnnotationCategory annotationCategory;

	public AnnotationConfig(AnnotationCategory cat) {
		this.annotationCategory=cat;
	}		
	
	public AnnotationCategory getAnnotationCategory() {
		return annotationCategory;
	}

}
