package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionPageView extends PageViewBase {

	FunctionPageView() {
		super(EntryPage.FUNCTION);
	}

	@Nonnull
	@Override
	public List<AnnotationCategory> getAnnotationCategoryWhiteList() {

		return Arrays.asList(
				AnnotationCategory.ALLERGEN,
				AnnotationCategory.FUNCTION_INFO,
				AnnotationCategory.GO_MOLECULAR_FUNCTION,
				AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				AnnotationCategory.ENZYME_REGULATION,
				AnnotationCategory.CATALYTIC_ACTIVITY,
				AnnotationCategory.COFACTOR,
				AnnotationCategory.PATHWAY,
				//AnnotationCategory.DISRUPTIVE_PHENOTYPE, no data in NP1
				AnnotationCategory.CAUTION,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	public List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<String> getXrefDbNameWhiteList() {

		return Arrays.asList("BRENDA", "CAZy", "KEGGPathway", "MEROPS", "PeroxiBase",
				"BioCyc", "Reactome","Pathway_Interaction_DB", "REBASE", "TCDB",
				"GeneWiki", "SABIO-RK", "GenomeRNAi", "GuidetoPHARMACOLOGY", "PRO","MoonProt","ESTHER", "SwissLipids");
	}
}
