package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Arrays;
import java.util.List;

public class MedicalPageConfig extends SimplePageConfig {

	private static final MedicalPageConfig INSTANCE = new MedicalPageConfig();

	public static MedicalPageConfig getInstance() { return INSTANCE; }

	private MedicalPageConfig() {
		super("Medical");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList(
				AnnotationCategory.DISEASE,
				AnnotationCategory.VARIANT_INFO,  // = POLYMORPHISM NP1
				//AnnotationCategory.BIOTECHNOLOGY, // now included in MISCELLANEOUS in NP2
				AnnotationCategory.PHARMACEUTICAL,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return Arrays.asList(AnnotationCategory.VARIANT); // = SEQ_VARIANT NP1
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList("GeneReviews", "CTD", "MIM", "DrugBank", "PharmGKB", "Orphanet",
				"Allergome", "DMDM", "BioMuta", "MalaCards" );
	}
}
