package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PeptidesPageView extends PageViewBase {

	PeptidesPageView() {
		super(EntryPage.PEPTIDES);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		// what else, this is what I see, but not all are in nextprot-viewers/edit/master/lib/featureConfig.json
		// some hardcoded somewhere ?
		return Arrays.asList(
					AnnotationCategory.MATURATION_PEPTIDE,
					AnnotationCategory.MATURE_PROTEIN,
					AnnotationCategory.ANTIBODY_MAPPING,
					AnnotationCategory.MODIFIED_RESIDUE,
					AnnotationCategory.CROSS_LINK,
					AnnotationCategory.PEPTIDE_MAPPING,
					AnnotationCategory.SRM_PEPTIDE_MAPPING);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList();
	}
}
