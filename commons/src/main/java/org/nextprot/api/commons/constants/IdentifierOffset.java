package org.nextprot.api.commons.constants;

/**
 * Some annotations are built from different tables (annotations, mapping_annotations, partnerships, xrefs,...).
 * To make sure to have no identifier having the same primary key value in two different tables 
 * we add an offset to those which are not from the "annotations" table
 * 
 * Same strategy for evidences providing from different tables (annotation_resource_assoc, identifier_resource_assoc, partnership_resource_assoc,...)
 * 
 * @author pmichel
 *
 */
public class IdentifierOffset {
	
	public final static long XREF_ANNOTATION_OFFSET = 100_000_000_000L;
	public final static long XREF_ANNOTATION_EVIDENCE_OFFSET = 200_000_000_000L;

	public final static long BINARY_INTERACTION_ANNOTATION_OFFSET = 300_000_000_000L;
	public final static long BINARY_INTERACTION_ANNOTATION_EVIDENCE_OFFSET = 400_000_000_000L;
	
	public final static long BIOPHYSICOCHEMICAL_ANNOTATION_OFFSET = 500_000_000_000L;
	// BIOPHYSICOCHEMICAL annotations have no evidence so no worry for evidence offset !
	
	public final static long PEPTIDE_MAPPING_ANNOTATION_OFFSET = 700_000_000_000L;
	public final static long PEPTIDE_MAPPING_ANNOTATION_EVIDENCE_OFFSET = 800_000_000_000L;
	
	public final static long ANTIBODY_MAPPING_ANNOTATION_OFFSET = 1_100_000_000_000L;
	public final static long ANTIBODY_MAPPING_ANNOTATION_EVIDENCE_OFFSET = 1_200_000_000_000L;

	public final static long XREF_PROPERTY_OFFSET = 1_300_000_000_000L;
	public final static long XREF_ENSEMBL_GENE_PROPERTY_OFFSET = 1_400_000_000_000L;
	public final static long XREF_ENSEMBL_PROTEIN_PROPERTY_OFFSET = 1_500_000_000_000L;
}
