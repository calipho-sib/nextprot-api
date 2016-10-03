package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.List;

public class ExonsPageConfig extends SimplePageConfig {

	private static final ExonsPageConfig INSTANCE = new ExonsPageConfig();

	private ExonsPageConfig() {
		super("Exons");
	}

	public static ExonsPageConfig getInstance() { return INSTANCE; }

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return new ArrayList<>();
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return new ArrayList<>();
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return new ArrayList<>();
	}
}
