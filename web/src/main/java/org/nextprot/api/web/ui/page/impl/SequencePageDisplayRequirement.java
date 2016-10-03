package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.web.ui.page.EntryPage;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SequencePageDisplayRequirement extends BasePageDisplayRequirement {

	SequencePageDisplayRequirement() {
		super(EntryPage.SEQUENCE);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.DOMAIN_INFO, // to check: war PTM in NP1
				AnnotationCategory.PTM_INFO,
				AnnotationCategory.SEQUENCE_CAUTION,
				AnnotationCategory.CAUTION
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {

		return Arrays.asList(

				AnnotationCategory.PROCESSING_PRODUCT, // check: was MOLECULE_PROCESSING in NP1
				AnnotationCategory.SIGNAL_PEPTIDE,
				AnnotationCategory.MATURATION_PEPTIDE,
				AnnotationCategory.MATURE_PROTEIN,
				AnnotationCategory.INITIATOR_METHIONINE,
				AnnotationCategory.TRANSIT_PEPTIDE,
				AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE,    // added by Pam (sub of transit)
				AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, // added by Pam (sub of transit)

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
				AnnotationCategory.INTRAMEMBRANE_REGION, 	// added by pam
				AnnotationCategory.MISCELLANEOUS_SITE, 		// added by Mathieu, correct ?
				//AnnotationCategory.CODING_SEQUENCE, 		// what is NP2 ?

				AnnotationCategory.DISULFIDE_BOND,
				AnnotationCategory.MODIFIED_RESIDUE,
				AnnotationCategory.CROSS_LINK,
				AnnotationCategory.GLYCOSYLATION_SITE,
				AnnotationCategory.LIPIDATION_SITE,
				AnnotationCategory.SELENOCYSTEINE,

				//AnnotationCategory.GENERIC_SITE, 			// generic cat added by pam, needed here
				AnnotationCategory.ACTIVE_SITE,
				AnnotationCategory.BINDING_SITE,
				AnnotationCategory.CLEAVAGE_SITE,
				AnnotationCategory.METAL_BINDING_SITE,

				AnnotationCategory.VARIANT,
				//AnnotationCategory.SEQ_VARIANT, 			// what in NP2 ?
				AnnotationCategory.MUTAGENESIS,
				AnnotationCategory.SEQUENCE_CONFLICT

				//AnnotationCategory.MISDEFINED_REGION, 		// what in NP2 ?
				//AnnotationCategory.NON_CONSECUTIVE_RESID, 	// what in NP2 ?
				//AnnotationCategory.NON_TERM_RESID, 		// what in NP2 ?
				//AnnotationCategory.UNSURE_RESID			// what in NP2 ?
		);
	}

	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList("CCDS", "eggNOG", "EMBL","Ensembl", "Gene3D", "GlycoSuiteDB", "HOGENOM", "HOVERGEN",
				"InParanoid", "InterPro", "KEGG", "MGI",
				"OMA", "OrthoDB", "PANTHER", "Pfam", "PhosphoSite", "PhylomeDB",
				"PIR", "PIRSF", "PMAP-CutDB", "PRINTS", "ProDom", "ProtClustDB", "PROSITE",
				"RefSeq", "SMART", "SUPFAM", "TIGRFAMS","UCSC", "Uniprot",
				"DNASU", "EvolutionaryTrace", "KO", "UniCarbKB", "ChiTaRS", "HAMAP", "TIGRFAMs",
				"TreeFam","DEPOD","GeneTree","BioMuta","PIRNR","SIGNOR","iPTMnet","SwissPalm");
	}
}
