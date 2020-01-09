package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

public class StructuresPageView extends PageViewBase {



	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.MISCELLANEOUS_REGION,
				AnnotationCategory.DOMAIN,
				AnnotationCategory.REPEAT,
				AnnotationCategory.CALCIUM_BINDING_REGION,
				AnnotationCategory.ZINC_FINGER_REGION,
				AnnotationCategory.DNA_BINDING_REGION,
				AnnotationCategory.NUCLEOTIDE_PHOSPHATE_BINDING_REGION,
				AnnotationCategory.COILED_COIL_REGION,
				AnnotationCategory.SHORT_SEQUENCE_MOTIF,
				AnnotationCategory.COMPOSITIONALLY_BIASED_REGION,
				AnnotationCategory.INTERACTING_REGION,
				AnnotationCategory.TOPOLOGY,
				AnnotationCategory.TOPOLOGICAL_DOMAIN,
				AnnotationCategory.TRANSMEMBRANE_REGION,
				AnnotationCategory.INTRAMEMBRANE_REGION, 
				AnnotationCategory.MISCELLANEOUS_SITE, 
				AnnotationCategory.ACTIVE_SITE,
				AnnotationCategory.BINDING_SITE,
				AnnotationCategory.CLEAVAGE_SITE,
				AnnotationCategory.METAL_BINDING_SITE,
				AnnotationCategory.VARIANT,
				AnnotationCategory.MUTAGENESIS,
				AnnotationCategory.SEQUENCE_CONFLICT,
				AnnotationCategory.BETA_STRAND,
				AnnotationCategory.HELIX,
				AnnotationCategory.TURN
		);
	}

	@Nonnull
	@Override
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {

		return Arrays.asList(DISPROT, PDB, PDB_SUM, PROTEOPEDIA, SMR, UNILECTIN);
	}

	@Override
	public String getLabel() {
		return "Structures";
	}

	@Override
	public String getLink() {
		return "structures";
	}
}
