DROP TABLE MAPPED_STATEMENTS;
CREATE TABLE MAPPED_STATEMENTS (


         entry_accession VARCHAR2(256),
         gene_name	 VARCHAR2(256),
         isoform_accession	 VARCHAR2(256),
         
         biological_subject_annot_hash VARCHAR2(256),
         
         annotation_category VARCHAR2(256),
         annot_cv_term_terminology VARCHAR2(256),
         annot_cv_term_name VARCHAR2(256),
         annot_cv_term_accession VARCHAR2(256),
         
         biological_object_type VARCHAR2(256),
         biological_object_accession VARCHAR2(256),
         biological_object_database VARCHAR2(256),
         biological_object_annot_hash VARCHAR2(256),
         
         annot_name VARCHAR2(256),
         annot_hash VARCHAR2(256),
         annot_description VARCHAR2(4000),

         annot_loc_begin_canonical_ref VARCHAR2(256),
         annot_loc_end_canonical_ref VARCHAR2(256),
         annot_loc_begin_genomic_ref VARCHAR2(256),
         annot_loc_end_genomic_ref VARCHAR2(256),

         annot_source_accession VARCHAR2(256),
         annot_source_database VARCHAR2(256),
         
         variant_origin VARCHAR2(256),
         variant_original_amino_acid VARCHAR2(256),
         variant_variation_amino_acid VARCHAR2(256),
         variant_original_genomic VARCHAR2(256),
         variant_variation_genomic VARCHAR2(256),
         variant_name_synonym_genomic VARCHAR2(256),
         variant_name_synonym_protein VARCHAR2(256),
         variant_name_synonym_isoform VARCHAR2(256),
         variant_name_synonym_error VARCHAR2(256),
         
         modified_entry_name VARCHAR2(256),
         evidence_source_accession VARCHAR2(256),
         reference_pubmed VARCHAR2(256)
);
GRANT SELECT ON MAPPED_STATEMENTS TO nxbed_read;