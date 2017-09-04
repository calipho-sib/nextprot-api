package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneIdentifiersPageView extends PageViewBase {

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

	@Override
	public String getLabel() {
		return "Gene Identifiers";
	}

	@Override
	public String getLink() {
		return "gene_identifiers";
	}
}
