package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Please keep in sync with specs in https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Expression+view+specs
 * @author pmichel
 *
 */
public class ExpressionPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.EXPRESSION_INFO, /*HPA, Uniprot*/
				AnnotationCategory.EXPRESSION_PROFILE,
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
				"Antibodypedia", "Bgee", "CleanEx", "GermOnline",
				"HPA","ExpressionAtlas","Genevisible");
	}

	@Override
	public String getLabel() {
		return "Expression";
	}

	@Override
	public String getLink() {
		return "expression";
	}
}
