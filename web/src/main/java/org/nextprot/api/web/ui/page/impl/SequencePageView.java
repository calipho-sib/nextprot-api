package org.nextprot.api.web.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;

public class SequencePageView extends PageViewBase {

	@Override
	public boolean keepUniprotEntryXref() {
		return true;
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getAnnotationCategoryWhiteList() {
		return Arrays.asList(
				AnnotationCategory.DOMAIN_INFO, 
				AnnotationCategory.PTM_INFO,
				AnnotationCategory.SEQUENCE_CAUTION,
				AnnotationCategory.CAUTION
		);
	}

	@Nonnull
	@Override
	protected List<AnnotationCategory> getFeatureCategoryWhiteList() {

		return Arrays.asList(

				// Processing group
				AnnotationCategory.SIGNAL_PEPTIDE,
				AnnotationCategory.MATURATION_PEPTIDE,
				AnnotationCategory.MATURE_PROTEIN,
				AnnotationCategory.INITIATOR_METHIONINE,
				AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE,    
				AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, 

				// Region group
				AnnotationCategory.DOMAIN,
				AnnotationCategory.MISCELLANEOUS_REGION,
				AnnotationCategory.REPEAT,
				AnnotationCategory.CALCIUM_BINDING_REGION,
				AnnotationCategory.ZINC_FINGER_REGION,
				AnnotationCategory.DNA_BINDING_REGION,
				AnnotationCategory.NUCLEOTIDE_PHOSPHATE_BINDING_REGION,
				AnnotationCategory.COILED_COIL_REGION,
				AnnotationCategory.SHORT_SEQUENCE_MOTIF,
				AnnotationCategory.COMPOSITIONALLY_BIASED_REGION,
				AnnotationCategory.INTERACTING_REGION,
				
				// Topology group
				AnnotationCategory.TOPOLOGICAL_DOMAIN,
				AnnotationCategory.TRANSMEMBRANE_REGION,
				AnnotationCategory.INTRAMEMBRANE_REGION, 	
				
				// Modified residue
				AnnotationCategory.DISULFIDE_BOND,
				AnnotationCategory.MODIFIED_RESIDUE,
				AnnotationCategory.CROSS_LINK,
				AnnotationCategory.GLYCOSYLATION_SITE,
				AnnotationCategory.LIPIDATION_SITE,
				AnnotationCategory.SELENOCYSTEINE,

				// Site group
				AnnotationCategory.MISCELLANEOUS_SITE,
				AnnotationCategory.ACTIVE_SITE,
				AnnotationCategory.BINDING_SITE,
				AnnotationCategory.CLEAVAGE_SITE,
				AnnotationCategory.METAL_BINDING_SITE,

				// Variation group
				AnnotationCategory.VARIANT,
				AnnotationCategory.MUTAGENESIS,

				// Conflict group
				AnnotationCategory.SEQUENCE_CONFLICT

		);
	}


	@Nonnull
	@Override
	protected List<String> getXrefDbNameWhiteList() {
		return Arrays.asList(
				
				// ENZYME AND PATHWAY DATABASES
				"SIGNOR",
				
				// FAMILY AND DOMAIN DATABASES
				"CDD", "Gene3D", "HAMAP", "InterPro", "PANTHER", "Pfam", "PIRSF", "PRINTS", "ProDom", "PROSITE", "SFLD", "SMART", "SUPFAM", "TIGRFAMs",
				
				// GENOME ANNOTATION DATABASES
				"Ensembl", "KEGG", "UCSC",
				
				// ORGANISM-SPECIFIC DATABASES
				"MGI",
				
				// OTHER
				"ChiTaRS", "EvolutionaryTrace", "PIRNR", "PMAP-CutDB",
				
				// PTM DATABASES
				"DEPOD", "iPTMnet", "PhosphoSitePlus", "SwissPalm", "UniCarbKB",
				
				// PHYLOGENOMIC DATABASES
				"eggNOG", "GeneTree", "HOGENOM", "HOVERGEN", "InParanoid", "KO", "OMA", "OrthoDB", "PhylomeDB", "TreeFam",
				
				// POLYMORPHISM AND MUTATION DATABASES
				"BioMuta",
				
				// PROTEIN FAMILY/GROUP DATABASES
				"IMGT_GENE-DB",
				
				// SEQUENCE DATABASES
				"CCDS", "EMBL", "PIR", "RefSeq", "UniProt",
				
				// PROTOCOLS AND MATERIALS DATABASES
				"DNASU"
			);
	}

	@Override
	public String getLabel() {
		return "Sequence";
	}

	@Override
	public String getLink() {
		return "sequence";
	}
}

