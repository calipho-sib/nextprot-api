package org.nextprot.api.core.utils.entry;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Arrays;

public class PeptidesPageConfig extends SimplePageConfig {

	private static final PeptidesPageConfig INSTANCE = new PeptidesPageConfig();

	public static PeptidesPageConfig getInstance() { return INSTANCE; }
	
	private PeptidesPageConfig() {
		
		annotations = Arrays.asList();

		// what else, this is what I see, but not all are in nextprot-viewers/edit/master/lib/featureConfig.json
		// some hardcoded somewhere ?
		features = Arrays.asList(
	               AnnotationCategory.MATURATION_PEPTIDE,
	               AnnotationCategory.MATURE_PROTEIN, 
	               AnnotationCategory.ANTIBODY_MAPPING,
	               AnnotationCategory.MODIFIED_RESIDUE,
	               AnnotationCategory.CROSS_LINK,
	               AnnotationCategory.PEPTIDE_MAPPING,
	               AnnotationCategory.SRM_PEPTIDE_MAPPING
		);
		
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
