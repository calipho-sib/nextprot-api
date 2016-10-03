package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneIdentifiersPageDisplayRequirement extends BasePageDisplayRequirement {

	private static final GeneIdentifiersPageDisplayRequirement INSTANCE = new GeneIdentifiersPageDisplayRequirement();

	public static GeneIdentifiersPageDisplayRequirement getInstance() { return INSTANCE; }

	private GeneIdentifiersPageDisplayRequirement() {
		super("Gene Identifiers");
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
