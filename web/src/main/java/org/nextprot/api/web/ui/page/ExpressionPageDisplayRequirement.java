package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final ExpressionPageDisplayRequirement INSTANCE = new ExpressionPageDisplayRequirement();

	private ExpressionPageDisplayRequirement() {
		super("Expression");
	}

	public static ExpressionPageDisplayRequirement getInstance() { return INSTANCE; }
	
	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.EXPRESSION_INFO, /*HPA, Uniprot*/
				AnnotationCategory.INDUCTION,
				AnnotationCategory.DEVELOPMENTAL_STAGE
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList(
				"ArrayExpress", "Bgee", "CleanEx", "Genevestigator", "GermOnline",
				"HPA", "Antibodypedia","ExpressionAtlas","Genevisible");
	}
}
