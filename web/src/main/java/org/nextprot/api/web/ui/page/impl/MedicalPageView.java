package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Please keep this class in sync with specs in https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Medical+view+specs
 * @author pmichel
 *
 */
public class MedicalPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.DISEASE,
				AnnotationCategory.VARIANT_INFO,  
				AnnotationCategory.PHARMACEUTICAL,
				AnnotationCategory.SMALL_MOLECULE_INTERACTION,
				AnnotationCategory.ALLERGEN,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(AnnotationCategory.VARIANT); 
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList(
				"GeneReviews", "CTD", "PharmGKB",
				"Allergome", "DMDM", "BioMuta", "MalaCards",
				"DisGeNET","OpenTargets" 
			);
	}
}
