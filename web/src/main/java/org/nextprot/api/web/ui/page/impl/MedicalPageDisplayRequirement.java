package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class MedicalPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final MedicalPageDisplayRequirement INSTANCE = new MedicalPageDisplayRequirement();

	public static MedicalPageDisplayRequirement getInstance() { return INSTANCE; }

	private MedicalPageDisplayRequirement() {
		super(EntryPage.MEDICAL);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.DISEASE,
				AnnotationCategory.VARIANT_INFO,  // = POLYMORPHISM NP1
				//AnnotationCategory.BIOTECHNOLOGY, // now included in MISCELLANEOUS in NP2
				AnnotationCategory.PHARMACEUTICAL,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(AnnotationCategory.VARIANT); // = SEQ_VARIANT NP1
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList("GeneReviews", "CTD", "MIM", "DrugBank", "PharmGKB", "Orphanet",
				"Allergome", "DMDM", "BioMuta", "MalaCards" );
	}
}
