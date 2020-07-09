package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

/**
 * Please keep in sync with specs in https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Function+view+specs
 * @author pmichel
 *
 */
public class FunctionPageView extends PageViewBase {

	@Nonnull
	@Override
	public List<AnnotationCategory> getAnnotationCategoryWhiteList() {

		return Arrays.asList(
				AnnotationCategory.FUNCTION_INFO,
				AnnotationCategory.CATALYTIC_ACTIVITY,
				AnnotationCategory.TRANSPORT_ACTIVITY,
				AnnotationCategory.ACTIVITY_REGULATION,
				AnnotationCategory.COFACTOR,
				AnnotationCategory.COFACTOR_INFO,
				AnnotationCategory.ABSORPTION_MAX,
				AnnotationCategory.ABSORPTION_NOTE,
				AnnotationCategory.ABSORPTION_MAX,
				AnnotationCategory.KINETIC_NOTE,
				AnnotationCategory.KINETIC_KM,
				AnnotationCategory.KINETIC_VMAX,
				AnnotationCategory.PH_DEPENDENCE,
				AnnotationCategory.TEMPERATURE_DEPENDENCE,
				AnnotationCategory.REDOX_POTENTIAL,
				AnnotationCategory.GO_MOLECULAR_FUNCTION,
				AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				AnnotationCategory.PATHWAY,
				AnnotationCategory.ALLERGEN,
				AnnotationCategory.CAUTION,
				AnnotationCategory.MISCELLANEOUS
		);
	}

	@Nonnull
	@Override
	public List<AnnotationCategory> getFeatureCategoryWhiteList() {
		return new ArrayList<>();
	}

	@Nonnull
	@Override
	public List<XrefDatabase> getXrefDatabaseWhiteList() {

		return Arrays.asList(
				GUIDETO_PHARMOCOLOGY, SWISS_LIPIDS, BIO_CYC, BRENDA, SABIO_RK,
				CAZY, ESTHER, MEROPS, MOON_PROT, PEROXIBASE, PHAROS, REBASE, SFLD,
				GENE_WIKI, GENOME_RNA_I, PRO, MOON_DB, BIO_GRID_ORCS
			);
	}

	@Override
	public String getLabel() {
		return "Function";
	}

	@Override
	public String getLink() {
		return "function";
	}
}
