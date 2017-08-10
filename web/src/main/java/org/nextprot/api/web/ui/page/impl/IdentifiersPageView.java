package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdentifiersPageView extends PageViewBase {

	IdentifiersPageView() {
		super(EntryPage.PROTEIN_IDENTIFIERS);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList("CCDS", "GeneCards", "GeneID",  "HGNC", "H-InvDB", "HPA", "HPRD","KEGG","LOC",
				"MIM",  "NextBio", "PDB", "PharmGKB", "PIR","RefSeq", "UCSC","UniGene",
				"ChEMBL");
	}
}
