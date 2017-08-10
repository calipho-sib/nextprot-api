package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalisationPageDisplayPredicate extends PageViewBase {

	LocalisationPageDisplayPredicate() {
		super(EntryPage.LOCALISATION);
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
