package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneIdentifiersPageDisplayPredicate extends PageViewBase {

	GeneIdentifiersPageDisplayPredicate() {
		super(EntryPage.GENE_IDENTIFIERS);
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
		return Arrays.asList("CCDS","GeneCards","GeneID","HGNC", "H-InvDB","KEGG", "LOC","MIM","RefSeq", "UniGene","UCSC");
	}
}
