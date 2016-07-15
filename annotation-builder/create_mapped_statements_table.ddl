DROP TABLE MAPPED_STATEMENTS_NEXT;
CREATE TABLE MAPPED_STATEMENTS_NEXT (

         nextprot_accession VARCHAR2(4000),

         entry_accession VARCHAR2(4000),
         gene_name	 VARCHAR2(4000),

                  LOCATION_BEGIN_MASTER VARCHAR2(4000),
         LOCATION_END_MASTER	 VARCHAR2(4000),
	
         isoform_accession	 VARCHAR2(4000),
         
         SUBJECT_ANNOT_ISO_IDS VARCHAR2(4000),
         SUBJECT_ANNOT_ENTRY_IDS VARCHAR2(4000),
         
         SUBJECT_ANNOT_ISO_UNAMES VARCHAR2(4000),
         SUBJECT_ANNOT_ENTRY_UNAMES VARCHAR2(4000),
         
         annotation_category VARCHAR2(4000),
         annot_cv_term_terminology VARCHAR2(4000),
         annot_cv_term_name VARCHAR2(4000),
         annot_cv_term_accession VARCHAR2(4000),
         
         biological_object_type VARCHAR2(4000),
         biological_object_accession VARCHAR2(4000),
         biological_object_database VARCHAR2(4000),
         
         OBJECT_ANNOT_ISO_IDS VARCHAR2(4000),
         OBJECT_ANNOT_ISO_UNAMES VARCHAR2(4000),
         
         OBJECT_ANNOT_ENTRY_IDS VARCHAR2(4000),
         OBJECT_ANNOT_ENTRY_UNAMES VARCHAR2(4000),
         
         ANNOT_ISO_ID VARCHAR2(4000),
         ANNOT_ISO_UNAME VARCHAR2(4000),
         
         ANNOT_ENTRY_ID VARCHAR2(4000),
         ANNOT_ENTRY_UNAME VARCHAR2(4000),
         
         annot_description VARCHAR2(4000),

         annot_loc_begin_canonical_ref VARCHAR2(4000),
         annot_loc_end_canonical_ref VARCHAR2(4000),
         annot_loc_begin_genomic_ref VARCHAR2(4000),
         annot_loc_end_genomic_ref VARCHAR2(4000),

         annot_source_accession VARCHAR2(4000),
         annot_source_database VARCHAR2(4000),

         exp_context_eco_detect_method VARCHAR2(4000),
         exp_context_property_intensity VARCHAR2(4000),
         EXP_CTX_PRPTY_PROTEIN_ORIGIN  VARCHAR2(4000),
         STATEMENT_QUALITY  VARCHAR2(4000),
         
         variant_origin VARCHAR2(4000),
         variant_original_amino_acid VARCHAR2(4000),
         variant_variation_amino_acid VARCHAR2(4000),
         variant_original_genomic VARCHAR2(4000),
         variant_variation_genomic VARCHAR2(4000),
         variant_name_synonym_genomic VARCHAR2(4000),
         variant_name_synonym_protein VARCHAR2(4000),
         variant_name_synonym_isoform VARCHAR2(4000),
         variant_name_synonym_error VARCHAR2(4000),
         
         modified_entry_name VARCHAR2(4000),
         evidence_source_accession VARCHAR2(4000),
         reference_pubmed VARCHAR2(4000),
         
         debug_note VARCHAR2(4000)
         
);
GRANT SELECT ON MAPPED_STATEMENTS_NEXT TO nxbed_read;