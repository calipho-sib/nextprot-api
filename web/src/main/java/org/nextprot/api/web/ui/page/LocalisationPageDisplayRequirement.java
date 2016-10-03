package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalisationPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final LocalisationPageDisplayRequirement INSTANCE = new LocalisationPageDisplayRequirement();

	public static LocalisationPageDisplayRequirement getInstance() { return INSTANCE; }

	private LocalisationPageDisplayRequirement() {
		super("Localisation");
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.SUBCELLULAR_LOCATION,
				AnnotationCategory.SUBCELLULAR_LOCATION_NOTE, // = NP1 SUBCELLULAR_LOCATION_INFO,
				AnnotationCategory.GO_CELLULAR_COMPONENT
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.TOPOLOGICAL_DOMAIN,
				AnnotationCategory.TRANSMEMBRANE_REGION,
				AnnotationCategory.INTRAMEMBRANE_REGION
		);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return new ArrayList<>();
	}

}
