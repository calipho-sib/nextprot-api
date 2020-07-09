package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

public class ProteomicsPageView extends PageViewBase {

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.MATURATION_PEPTIDE,
				AnnotationCategory.MATURE_PROTEIN,
				AnnotationCategory.INITIATOR_METHIONINE,
				AnnotationCategory.SIGNAL_PEPTIDE,
				AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE,
				AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE,

				AnnotationCategory.MODIFIED_RESIDUE,
				AnnotationCategory.DISULFIDE_BOND,
				AnnotationCategory.GLYCOSYLATION_SITE,
				AnnotationCategory.LIPIDATION_SITE,
				AnnotationCategory.CROSS_LINK,
				AnnotationCategory.SELENOCYSTEINE,
				
				AnnotationCategory.ANTIBODY_MAPPING,
				
				AnnotationCategory.PEPTIDE_MAPPING,
				AnnotationCategory.SRM_PEPTIDE_MAPPING
				
		);
	}

	@Nonnull
	@Override
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {
		
		return Arrays.asList(
				ABCD, CPTAC, DOSAC_COBS_2DPAGE, JPOST, OGP, REPRODUCTION_2DPAGE, SWISS_2DPAGE, UCD_2DPAGE,
				PHOSPHO_SITE_PLUS,
				EPD, MASSIVE, MAX_QB, PAX_DB, PEPTIDE_ATLAS, PRIDE, TOP_DOWN_PROTEOMICS,
				PROTEOMES, PROTEOMICS_DB, METOSITE, SWISS_PALM);
	}

	@Override
	public String getLabel() {
		return "Proteomics";
	}

	@Override
	public String getLink() {
		return "proteomics";
	}
}
