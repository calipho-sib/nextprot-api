package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.utils.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Please keep this class in sync with https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Localization+view+specs
 * @author pmichel
 *
 */
public class LocalisationPageView extends PageViewBase {

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
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {
		return new ArrayList<>();
	}

	@Override
	public String getLabel() {
		return "Localization";
	}

	@Override
	public String getLink() {
		return "localization";
	}
}
