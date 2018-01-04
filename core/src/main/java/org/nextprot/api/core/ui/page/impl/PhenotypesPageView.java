package org.nextprot.api.core.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PhenotypesPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.PHENOTYPIC_VARIATION);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList();
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList();
	}

	@Override
	public String getLabel() {
		return "Phenotypes";
	}

	@Override
	public String getLink() {
		return "phenotypes";
	}
}
