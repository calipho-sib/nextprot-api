package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Arrays;
import java.util.List;

public class PhenotypesPageConfig extends SimplePageConfig {

	private static final PhenotypesPageConfig INSTANCE = new PhenotypesPageConfig();

	public static PhenotypesPageConfig getInstance() { return INSTANCE; }

	private PhenotypesPageConfig() {
		super("Phenotypes");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList(
				AnnotationCategory.PHENOTYPIC_VARIATION);
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return Arrays.asList();
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList();
	}
}
