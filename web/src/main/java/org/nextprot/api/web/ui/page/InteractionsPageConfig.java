package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Arrays;
import java.util.List;

public class InteractionsPageConfig extends SimplePageConfig {

	private static final InteractionsPageConfig INSTANCE = new InteractionsPageConfig();

	public static InteractionsPageConfig getInstance() { return INSTANCE; }

	private InteractionsPageConfig() {
		super("Interactions");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return Arrays.asList(
				AnnotationCategory.INTERACTION_INFO, // = NP1 SUBUNIT,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
		return Arrays.asList(AnnotationCategory.INTERACTING_REGION);
	}

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList(
				"BindingDB","DIP","IntAct","MINT","STRING",
				"SignaLink", "BioGrid","SIGNOR");
	}
}
