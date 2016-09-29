package org.nextprot.api.web.ui.page;

import java.util.ArrayList;
import java.util.Arrays;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

public class MedicalPageConfig extends SimplePageConfig {

	private static final MedicalPageConfig INSTANCE = new MedicalPageConfig();

	public static MedicalPageConfig getInstance() { return INSTANCE; }
	
	private MedicalPageConfig() {
		
		annotations = Arrays.asList(
	               AnnotationCategory.DISEASE,
	               AnnotationCategory.VARIANT_INFO,  // = POLYMORPHISM NP1 
			       //AnnotationCategory.BIOTECHNOLOGY, // now included in MISCELLANEOUS in NP2
	               AnnotationCategory.PHARMACEUTICAL,
	               AnnotationCategory.MISCELLANEOUS
		);
		
		features = Arrays.asList(AnnotationCategory.VARIANT); // = SEQ_VARIANT NP1
		
		xrefs = Arrays.asList("GeneReviews", "CTD", "MIM", "DrugBank", "PharmGKB", "Orphanet",
	               "Allergome", "DMDM", "BioMuta", "MalaCards" );

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
