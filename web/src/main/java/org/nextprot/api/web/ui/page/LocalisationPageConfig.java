package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalisationPageConfig extends SimplePageConfig {

	private static final LocalisationPageConfig INSTANCE = new LocalisationPageConfig();

	public static LocalisationPageConfig getInstance() { return INSTANCE; }

	private LocalisationPageConfig() {
		super("Localisation");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList(
				AnnotationCategory.SUBCELLULAR_LOCATION,
				AnnotationCategory.SUBCELLULAR_LOCATION_NOTE, // = NP1 SUBCELLULAR_LOCATION_INFO,
				AnnotationCategory.GO_CELLULAR_COMPONENT
		);
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return Arrays.asList(
				AnnotationCategory.TOPOLOGICAL_DOMAIN,
				AnnotationCategory.TRANSMEMBRANE_REGION,
				AnnotationCategory.INTRAMEMBRANE_REGION
		);
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return new ArrayList<>();
	}

}
