package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProteomicsPageConfig extends SimplePageConfig {

	private static final ProteomicsPageConfig INSTANCE = new ProteomicsPageConfig();

	public static ProteomicsPageConfig getInstance() { return INSTANCE; }

	private ProteomicsPageConfig() {
		super("Proteomics");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return new ArrayList<>();
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
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

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList(
				"Aarhus/Ghent-2DPAGE","Cornea-2DPAGE","DOSAC-COBS-2DPAGE","OGP","PHCI-2DPAGE",
				"PMMA-2DPAGE", "REPRODUCTION-2DPAGE", "SWISS-2DPAGE", "Siena-2DPAGE", "UCD-2DPAGE",
				"PRIDE", "PeptideAtlas",
				"GlycoSuiteDB", "PhosphoSite",
				"PaxDb", "ProMEX", "MaxQB", "Proteomes","TopDownProteomics","EPD");
	}
}
