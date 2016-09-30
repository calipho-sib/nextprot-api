package org.nextprot.api.core.utils.entry;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;

public class LocalisationPageConfig extends SimplePageConfig {

	private static final LocalisationPageConfig INSTANCE = new LocalisationPageConfig();

	public static LocalisationPageConfig getInstance() { return INSTANCE; }
	
	private LocalisationPageConfig() {
		
		annotations = Arrays.asList(
    	           AnnotationCategory.SUBCELLULAR_LOCATION,
    	           AnnotationCategory.SUBCELLULAR_LOCATION_NOTE, // = NP1 SUBCELLULAR_LOCATION_INFO,
	               AnnotationCategory.GO_CELLULAR_COMPONENT
		);
		
		features = Arrays.asList(                 
				AnnotationCategory.TOPOLOGICAL_DOMAIN, 
                AnnotationCategory.TRANSMEMBRANE_REGION,
                AnnotationCategory.INTRAMEMBRANE_REGION
        );
		
		xrefs = new ArrayList<>();

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
