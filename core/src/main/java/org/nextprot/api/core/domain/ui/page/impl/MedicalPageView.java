package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

/**
 * Please keep this class in sync with specs in https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Medical+view+specs
 * @author pmichel
 *
 */
public class MedicalPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.DISEASE,
				AnnotationCategory.VARIANT_INFO,  
				AnnotationCategory.PHARMACEUTICAL,
				AnnotationCategory.SMALL_MOLECULE_INTERACTION,
				AnnotationCategory.ALLERGEN,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(AnnotationCategory.VARIANT); 
	}

	@Nonnull
	@Override
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {
		return Arrays.asList(
				DRUG_CENTRAL, GENE_REVIEWS, CTD, PHARM_GKB,
				ALLERGOME, DMDM, BIO_MUTA, MALA_CARDS,NIAGADS,
				DIS_GE_NET, OPEN_TARGETS, DECIPHER
			);
	}

	@Override
	public String getLabel() {
		return "Medical";
	}

	@Override
	public String getLink() {
		return "medical";
	}
}
