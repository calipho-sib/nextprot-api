package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

/**
 * Please keep this class in sync with specs in https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Interactions+view+specs
 * @author pmichel
 *
 */
public class InteractionsPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				
				AnnotationCategory.INTERACTION_INFO, 
				AnnotationCategory.ENZYME_REGULATION,
				AnnotationCategory.GO_MOLECULAR_FUNCTION, // further refined
				AnnotationCategory.BINARY_INTERACTION,
				AnnotationCategory.COFACTOR,
				AnnotationCategory.COFACTOR_INFO,
				AnnotationCategory.SMALL_MOLECULE_INTERACTION,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.MISCELLANEOUS_REGION,
				AnnotationCategory.CALCIUM_BINDING_REGION,
				AnnotationCategory.DNA_BINDING_REGION,
				AnnotationCategory.NUCLEOTIDE_PHOSPHATE_BINDING_REGION,
				AnnotationCategory.INTERACTING_REGION,
				AnnotationCategory.BINDING_SITE,
				AnnotationCategory.METAL_BINDING_SITE
			);
	}

	@Nonnull
	@Override
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {

		return Arrays.asList(BINDING_DB, DIP, INT_ACT, MINT, STRING, SIGNA_LINK, BIO_GRID, SIGNOR, CORUM, COMPLEX_PORTAL, RHEA);
	}

	@Override
	public String getLabel() {
		return "Interactions";
	}

	@Override
	public String getLink() {
		return "interactions";
	}
}
