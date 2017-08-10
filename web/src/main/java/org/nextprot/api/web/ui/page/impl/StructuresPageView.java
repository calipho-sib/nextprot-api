package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StructuresPageView extends PageViewBase {

	@Override
	public boolean doDisplayPage(@Nonnull Entry entry) {

		return entry.getXrefs().stream()
				.filter(xref -> !filterOutXref(xref))
				.anyMatch(xr -> getXrefDbNameWhiteList().contains(xr.getDatabaseName()));
	}

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
				AnnotationCategory.INTRAMEMBRANE_REGION, // added by pam
				AnnotationCategory.MISCELLANEOUS_SITE, // added by Mathieu
				//AnnotationCategory.CODING_SEQUENCE, // what is NP2 ?
				//AnnotationCategory.GENERIC_SITE, // generci cat added by pam, needed here
				AnnotationCategory.ACTIVE_SITE,
				AnnotationCategory.BINDING_SITE,
				AnnotationCategory.CLEAVAGE_SITE,
				AnnotationCategory.METAL_BINDING_SITE,
				AnnotationCategory.VARIANT,
				//AnnotationCategory.SEQ_VARIANT, // what in NP2
				AnnotationCategory.MUTAGENESIS,
				AnnotationCategory.SEQUENCE_CONFLICT,
				//AnnotationCategory.SECONDARY_STRUCTURE, // generic cat, needed here ?
				AnnotationCategory.BETA_STRAND,
				AnnotationCategory.HELIX,
				AnnotationCategory.TURN
		);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList("PDB","PDBsum", "ProteinModelPortal","HSSP", "SMR", "ModBase", "DisProt");
	}
}
