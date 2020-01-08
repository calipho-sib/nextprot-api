package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

public class IdentifiersPageView extends PageViewBase {

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
		return Arrays.asList(
				CCDS, GENE_CARDS, GENE_ID, HGNC, H_INV_DB, HPA, HPRD, KEGG, LOC,
				MIM,  NEXT_BIO, PDB, PHARM_GKB, PIR, REF_SEQ, UCSC,
				CH_EMBL);
	}

	@Override
	public String getLabel() {
		return "Identifiers";
	}

	@Override
	public String getLink() {
		return "identifiers";
	}
}
