DROP TABLE MAPPED_STATEMENTS;
CREATE TABLE MAPPED_STATEMENTS (

         nextprot_accession VARCHAR2(4000),
         entry_accession VARCHAR2(4000),
         gene_name	 VARCHAR2(4000),
         isoform_accession	 VARCHAR2(4000),
         
         biological_subject_annot_hash VARCHAR2(4000),
         biological_subject_annot_name VARCHAR2(4000),
         
         annotation_category VARCHAR2(4000),
         annot_cv_term_terminology VARCHAR2(4000),
         annot_cv_term_name VARCHAR2(4000),
         annot_cv_term_accession VARCHAR2(4000),
         
         biological_object_type VARCHAR2(4000),
         biological_object_accession VARCHAR2(4000),
         biological_object_database VARCHAR2(4000),
         biological_object_annot_hash VARCHAR2(4000),
         
         annot_name VARCHAR2(4000),
         annot_hash VARCHAR2(4000),
         annot_description VARCHAR2(4000),

         annot_loc_begin_canonical_ref VARCHAR2(4000),
         annot_loc_end_canonical_ref VARCHAR2(4000),
         annot_loc_begin_genomic_ref VARCHAR2(4000),
         annot_loc_end_genomic_ref VARCHAR2(4000),

         annot_source_accession VARCHAR2(4000),
         annot_source_database VARCHAR2(4000),

         exp_context_eco_detect_method VARCHAR2(4000),
         exp_context_property_intensity VARCHAR2(4000),
         
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
         reference_pubmed VARCHAR2(4000)
);
GRANT SELECT ON MAPPED_STATEMENTS TO nxbed_read;