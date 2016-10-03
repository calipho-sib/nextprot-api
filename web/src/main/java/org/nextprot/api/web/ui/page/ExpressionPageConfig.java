package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionPageConfig extends SimplePageConfig {

	private static final ExpressionPageConfig INSTANCE = new ExpressionPageConfig();

	private ExpressionPageConfig() {
		super("Expression");
	}

	public static ExpressionPageConfig getInstance() { return INSTANCE; }
	
	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList(
				AnnotationCategory.EXPRESSION_INFO, /*HPA, Uniprot*/
				AnnotationCategory.INDUCTION,
				AnnotationCategory.DEVELOPMENTAL_STAGE
		);
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return new ArrayList<>();
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList(
				"ArrayExpress", "Bgee", "CleanEx", "Genevestigator", "GermOnline",
				"HPA", "Antibodypedia","ExpressionAtlas","Genevisible");
	}
}
