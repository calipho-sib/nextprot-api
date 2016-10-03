package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ExonsPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final ExonsPageDisplayRequirement INSTANCE = new ExonsPageDisplayRequirement();

	private ExonsPageDisplayRequirement() {
		super("Exons");
	}

	public static ExonsPageDisplayRequirement getInstance() { return INSTANCE; }

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
}
