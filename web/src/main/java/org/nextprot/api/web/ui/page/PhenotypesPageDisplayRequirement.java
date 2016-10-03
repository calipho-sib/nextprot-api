package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PhenotypesPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final PhenotypesPageDisplayRequirement INSTANCE = new PhenotypesPageDisplayRequirement();

	public static PhenotypesPageDisplayRequirement getInstance() { return INSTANCE; }

	private PhenotypesPageDisplayRequirement() {
		super("Phenotypes");
	}

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
}
