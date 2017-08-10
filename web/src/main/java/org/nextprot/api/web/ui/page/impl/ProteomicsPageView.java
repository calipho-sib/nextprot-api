package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProteomicsPageView extends PageViewBase {

	ProteomicsPageView() {
		super(EntryPage.PROTEOMICS);
	}

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
				AnnotationCategory.TRANSIT_PEPTIDE,

				AnnotationCategory.DISULFIDE_BOND,
				AnnotationCategory.MODIFIED_RESIDUE,
				AnnotationCategory.CROSS_LINK,
				AnnotationCategory.GLYCOSYLATION_SITE,
				AnnotationCategory.LIPIDATION_SITE,
				AnnotationCategory.SELENOCYSTEINE
		);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList(
				"Aarhus/Ghent-2DPAGE","Cornea-2DPAGE","DOSAC-COBS-2DPAGE","OGP","PHCI-2DPAGE",
				"PMMA-2DPAGE", "REPRODUCTION-2DPAGE", "SWISS-2DPAGE", "Siena-2DPAGE", "UCD-2DPAGE",
				"PRIDE", "PeptideAtlas",
				"GlycoSuiteDB", "PhosphoSite",
				"PaxDb", "ProMEX", "MaxQB", "Proteomes","TopDownProteomics","EPD");
	}
}
