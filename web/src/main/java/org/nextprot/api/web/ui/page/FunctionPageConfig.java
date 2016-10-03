package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionPageConfig extends SimplePageConfig {

	private static final FunctionPageConfig INSTANCE = new FunctionPageConfig();

	private FunctionPageConfig() {
		super("Function");
	}

	public static FunctionPageConfig getInstance() { return INSTANCE; }

	@Override
	public List<AnnotationCategory> getSelectedAnnotationCategoryList() {

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

	@Override
	public List<AnnotationCategory> getSelectedFeatureList() {
		return new ArrayList<>();
	}

	@Override
	public List<String> getSelectedXrefDbNameList() {

		return Arrays.asList("BRENDA", "CAZy", "KEGGPathway", "MEROPS", "PeroxiBase",
				"BioCyc", "Reactome","Pathway_Interaction_DB", "REBASE", "TCDB",
				"GeneWiki", "SABIO-RK", "GenomeRNAi", "GuidetoPHARMACOLOGY", "PRO","MoonProt","ESTHER", "SwissLipids");
	}
}
