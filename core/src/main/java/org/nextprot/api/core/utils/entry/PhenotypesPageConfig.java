package org.nextprot.api.core.utils.entry;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Arrays;

public class PhenotypesPageConfig extends SimplePageConfig {

	private static final PhenotypesPageConfig INSTANCE = new PhenotypesPageConfig();

	public static PhenotypesPageConfig getInstance() { return INSTANCE; }
	
	private PhenotypesPageConfig() {
		
		annotations = Arrays.asList(
				AnnotationCategory.PHENOTYPIC_VARIATION);

		features = Arrays.asList();
		
		xrefs = Arrays.asList();

	}
	
	@Override
	public boolean filterOutAnnotation(Annotation a) {
		return true;
	}

	@Override
	public boolean filterOutXref(DbXref x) {
		return true;
	}

}
