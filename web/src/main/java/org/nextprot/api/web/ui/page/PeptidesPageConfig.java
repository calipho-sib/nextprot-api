package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Arrays;
import java.util.List;

public class PeptidesPageConfig extends SimplePageConfig {

	private static final PeptidesPageConfig INSTANCE = new PeptidesPageConfig();

	public static PeptidesPageConfig getInstance() { return INSTANCE; }

	private PeptidesPageConfig() {
		super("Peptides");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList();
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
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

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList();
	}
}
