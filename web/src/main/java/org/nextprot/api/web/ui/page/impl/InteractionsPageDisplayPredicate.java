package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class InteractionsPageDisplayPredicate extends PageDisplayBasePredicate {

	InteractionsPageDisplayPredicate() {
		super(EntryPage.INTERACTIONS);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.INTERACTION_INFO, // = NP1 SUBUNIT,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(AnnotationCategory.INTERACTING_REGION);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList(
				"BindingDB","DIP","IntAct","MINT","STRING",
				"SignaLink", "BioGrid","SIGNOR");
	}
}
