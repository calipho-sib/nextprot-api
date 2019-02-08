package org.nextprot.api.core.domain.ui.page.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static org.nextprot.api.core.service.dbxref.XrefDatabase.*;

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
	protected List<XrefDatabase> getXrefDatabaseWhiteList() {
		return Arrays.asList(
				
				// ENZYME AND PATHWAY DATABASES
				SIGNOR,
				
				// FAMILY AND DOMAIN DATABASES
				CDD, GENE_3D, HAMAP, INTER_PRO, PANTHER, PFAM, PIRSF, PRINTS,
				PRO_DOM, PROSITE, SFLD, SMART, SUPFAM, TIGRFAMS,
				
				// GENOME ANNOTATION DATABASES
				ENSEMBL, KEGG, UCSC,
				
				// ORGANISM-SPECIFIC DATABASES
				MGI, EU_PATH_DB,
				
				// OTHER
				CHITARS, EVOLUTIONARY_TRACE, PIRNR, PMAP_CUT_DB, PIRSR,
				
				// PTM DATABASES
				DEPOD, I_PTM_NET, PHOSPHO_SITE_PLUS, SWISS_PALM, UNI_CARB_KB, GLY_CONNECT, CARBONYL_DB,
				
				// PHYLOGENOMIC DATABASES
				EGG_NOG, GENE_TREE, HOGENOM, HOVERGEN, IN_PARANOID, KO, OMA, ORTHO_DB, PHYLOM_DB, TREE_FAM,
				
				// POLYMORPHISM AND MUTATION DATABASES
				BIO_MUTA,
				
				// PROTEIN FAMILY/GROUP DATABASES
				IMGT_GENE_DB, UNILECTIN,
				
				// PROTEIN-PROTEIN INTERACTION DATABASES
				ELM,
				
				// SEQUENCE DATABASES
				CCDS, EMBL, PIR, REF_SEQ, UNIPROT,
				
				// PROTOCOLS AND MATERIALS DATABASES
				DNASU
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

