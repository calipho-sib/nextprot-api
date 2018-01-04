package org.nextprot.api.core.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProteomicsPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.MATURATION_PEPTIDE,
				AnnotationCategory.MATURE_PROTEIN,
				AnnotationCategory.INITIATOR_METHIONINE,
				AnnotationCategory.SIGNAL_PEPTIDE,
				AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE,
				AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE,

				AnnotationCategory.MODIFIED_RESIDUE,
				AnnotationCategory.DISULFIDE_BOND,
				AnnotationCategory.GLYCOSYLATION_SITE,
				AnnotationCategory.LIPIDATION_SITE,
				AnnotationCategory.CROSS_LINK,
				AnnotationCategory.SELENOCYSTEINE,
				
				AnnotationCategory.ANTIBODY_MAPPING,
				
				AnnotationCategory.PEPTIDE_MAPPING,
				AnnotationCategory.SRM_PEPTIDE_MAPPING
				
		);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		
		return Arrays.asList(
				"DOSAC-COBS-2DPAGE","OGP", "REPRODUCTION-2DPAGE", "SWISS-2DPAGE", "UCD-2DPAGE",
				"PhosphoSitePlus",
				"EPD", "MaxQB", "PaxDb", "PeptideAtlas","PRIDE","TopDownProteomics");
	}

	@Override
	public String getLabel() {
		return "Proteomics";
	}

	@Override
	public String getLink() {
		return "proteomics";
	}
}
