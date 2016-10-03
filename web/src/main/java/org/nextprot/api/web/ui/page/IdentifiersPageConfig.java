package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdentifiersPageConfig extends SimplePageConfig {

	private static final IdentifiersPageConfig INSTANCE = new IdentifiersPageConfig();

	public static IdentifiersPageConfig getInstance() { return INSTANCE; }

	private IdentifiersPageConfig() {
		super("Protein Identifiers");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return new ArrayList<>();
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return new ArrayList<>();
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList("CCDS", "GeneCards", "GeneID",  "HGNC", "H-InvDB", "HPA", "HPRD","KEGG","LOC",
				"MIM",  "NextBio", "PDB", "PharmGKB", "PIR","RefSeq", "UCSC","UniGene",
				"ChEMBL");
	}
}
