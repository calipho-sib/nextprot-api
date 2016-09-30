package org.nextprot.api.core.utils.entry;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpressionPageConfig extends SimplePageConfig {

	private static final ExpressionPageConfig INSTANCE = new ExpressionPageConfig();

	public static ExpressionPageConfig getInstance() { return INSTANCE; }
	
	private ExpressionPageConfig() {
		
		annotations = Arrays.asList(
	               AnnotationCategory.EXPRESSION_INFO, /*HPA, Uniprot*/
	               AnnotationCategory.INDUCTION,
	               AnnotationCategory.DEVELOPMENTAL_STAGE
		);
		
		features = new ArrayList<>();
		
		xrefs = Arrays.asList(
				"ArrayExpress", "Bgee", "CleanEx", "Genevestigator", "GermOnline", 
				"HPA", "Antibodypedia","ExpressionAtlas","Genevisible");

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
