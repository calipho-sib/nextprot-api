package org.nextprot.api.core.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExonsPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return new ArrayList<>();
	}

	@Override
	public String getLabel() {
		return "Exons";
	}

	@Override
	public String getLink() {
		return "exons";
	}
}
