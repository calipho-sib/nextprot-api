package org.nextprot.api.core.domain.ui.page.impl;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.NIAGADS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

public class ExonsPageView extends PageViewBase {

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
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {
		return Arrays.asList(NIAGADS);
	}

	@Override
	public String getLabel() {
		return "Exons";
	}

	@Override
	public String getLink() {
		return "exons";
	}
}
