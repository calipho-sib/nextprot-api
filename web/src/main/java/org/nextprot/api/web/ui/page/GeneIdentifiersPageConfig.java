package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneIdentifiersPageConfig extends SimplePageConfig {

	private static final GeneIdentifiersPageConfig INSTANCE = new GeneIdentifiersPageConfig();

	public static GeneIdentifiersPageConfig getInstance() { return INSTANCE; }

	private GeneIdentifiersPageConfig() {
		super("Gene Identifiers");
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
		return Arrays.asList("CCDS","GeneCards","GeneID","HGNC", "H-InvDB","KEGG", "LOC","MIM","RefSeq", "UniGene","UCSC");
	}
}
