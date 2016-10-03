package org.nextprot.api.web.ui.page;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StructuresPageConfig extends SimplePageConfig {

	private static final StructuresPageConfig INSTANCE = new StructuresPageConfig();

	public static StructuresPageConfig getInstance() { return INSTANCE; }

	private StructuresPageConfig() {
		super("Structures");
	}

	@Override
	protected List<AnnotationCategory> getSelectedAnnotationCategoryList() {
		return new ArrayList<>();
	}

	@Override
	protected List<AnnotationCategory> getSelectedFeatureList() {
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

	@Override
	protected List<String> getSelectedXrefDbNameList() {
		return Arrays.asList("PDB","PDBsum", "ProteinModelPortal","HSSP", "SMR", "ModBase", "DisProt");
	}
}
