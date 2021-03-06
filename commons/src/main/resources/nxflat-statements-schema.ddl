DROP TABLE IF EXISTS nxflat.ENTRY_MAPPED_STATEMENTS;
CREATE TABLE nxflat.ENTRY_MAPPED_STATEMENTS (
	ANNOTATION_CATEGORY VARCHAR(10000),
	ANNOTATION_ID VARCHAR(10000),
	ANNOTATION_NAME VARCHAR(10000),
	ANNOTATION_OBJECT_SPECIES VARCHAR(10000),
	ANNOTATION_SUBJECT_SPECIES VARCHAR(10000),
	ANNOT_CV_TERM_ACCESSION VARCHAR(10000),
	ANNOT_CV_TERM_NAME VARCHAR(10000),
	ANNOT_CV_TERM_TERMINOLOGY VARCHAR(10000),
	ANNOT_DESCRIPTION VARCHAR(10000),
	ANNOT_SOURCE_ACCESSION VARCHAR(10000),
	ASSIGMENT_METHOD VARCHAR(10000),
	ASSIGNED_BY VARCHAR(10000),
	BIOLOGICAL_OBJECT_ACCESSION VARCHAR(10000),
	BIOLOGICAL_OBJECT_DATABASE VARCHAR(10000),
	BIOLOGICAL_OBJECT_NAME VARCHAR(10000),
	BIOLOGICAL_OBJECT_TYPE VARCHAR(10000),
	DEBUG_INFO VARCHAR(10000),
	ENTRY_ACCESSION VARCHAR(10000),
	EVIDENCE_CODE VARCHAR(10000),
	EVIDENCE_INTENSITY VARCHAR(10000),
	EVIDENCE_NOTE VARCHAR(10000),
	EVIDENCE_PROPERTIES VARCHAR(10000),
	EVIDENCE_QUALITY VARCHAR(10000),
	EVIDENCE_STATEMENT_REF VARCHAR(10000),
	EXP_CONTEXT_ECO_DETECT_METHOD VARCHAR(10000),
	EXP_CONTEXT_ECO_ISS VARCHAR(10000),
	EXP_CONTEXT_ECO_MUTATION VARCHAR(10000),
	EXTRA_FIELDS VARCHAR(10000),
	GENE_NAME VARCHAR(10000),
	ISOFORM_CANONICAL VARCHAR(10000),
	IS_NEGATIVE VARCHAR(10000),
	LOCATION_BEGIN VARCHAR(10000),
	LOCATION_BEGIN_MASTER VARCHAR(10000),
	LOCATION_END VARCHAR(10000),
	LOCATION_END_MASTER VARCHAR(10000),
	NEXTPROT_ACCESSION VARCHAR(10000),
	OBJECT_ANNOTATION_IDS VARCHAR(10000),
	OBJECT_ANNOT_ENTRY_UNAMES VARCHAR(10000),
	OBJECT_ANNOT_ISO_UNAMES VARCHAR(10000),
	OBJECT_STATEMENT_IDS VARCHAR(10000),
	RAW_STATEMENT_ID VARCHAR(10000),
	REFERENCE_ACCESSION VARCHAR(10000),
	REFERENCE_DATABASE VARCHAR(10000),
	RESOURCE_TYPE VARCHAR(10000),
	SOURCE VARCHAR(10000),
	STATEMENT_ID VARCHAR(10000),
	SUBJECT_ANNOTATION_IDS VARCHAR(10000),
	SUBJECT_STATEMENT_IDS VARCHAR(10000),
	TARGET_ISOFORMS VARCHAR(10000),
	VARIANT_ORIGINAL_AMINO_ACID VARCHAR(10000),
	VARIANT_VARIATION_AMINO_ACID VARCHAR(10000));
CREATE INDEX ENTRY_MAPP_ENTRY_AC_IDX ON nxflat.ENTRY_MAPPED_STATEMENTS ( ENTRY_ACCESSION );
CREATE INDEX ENTRY_MAPP_ANNOT_ID_IDX ON nxflat.ENTRY_MAPPED_STATEMENTS ( ANNOTATION_ID );

DROP TABLE IF EXISTS nxflat.RAW_STATEMENTS;
CREATE TABLE nxflat.RAW_STATEMENTS (
	ANNOTATION_CATEGORY VARCHAR(10000),
	ANNOTATION_ID VARCHAR(10000),
	ANNOTATION_NAME VARCHAR(10000),
	ANNOTATION_OBJECT_SPECIES VARCHAR(10000),
	ANNOTATION_SUBJECT_SPECIES VARCHAR(10000),
	ANNOT_CV_TERM_ACCESSION VARCHAR(10000),
	ANNOT_CV_TERM_NAME VARCHAR(10000),
	ANNOT_CV_TERM_TERMINOLOGY VARCHAR(10000),
	ANNOT_DESCRIPTION VARCHAR(10000),
	ANNOT_SOURCE_ACCESSION VARCHAR(10000),
	ASSIGMENT_METHOD VARCHAR(10000),
	ASSIGNED_BY VARCHAR(10000),
	BIOLOGICAL_OBJECT_ACCESSION VARCHAR(10000),
	BIOLOGICAL_OBJECT_DATABASE VARCHAR(10000),
	BIOLOGICAL_OBJECT_NAME VARCHAR(10000),
	BIOLOGICAL_OBJECT_TYPE VARCHAR(10000),
	DEBUG_INFO VARCHAR(10000),
	ENTRY_ACCESSION VARCHAR(10000),
	EVIDENCE_CODE VARCHAR(10000),
	EVIDENCE_INTENSITY VARCHAR(10000),
	EVIDENCE_NOTE VARCHAR(10000),
	EVIDENCE_PROPERTIES VARCHAR(10000),
	EVIDENCE_QUALITY VARCHAR(10000),
	EVIDENCE_STATEMENT_REF VARCHAR(10000),
	EXP_CONTEXT_ECO_DETECT_METHOD VARCHAR(10000),
	EXP_CONTEXT_ECO_ISS VARCHAR(10000),
	EXP_CONTEXT_ECO_MUTATION VARCHAR(10000),
	EXTRA_FIELDS VARCHAR(10000),
	GENE_NAME VARCHAR(10000),
	ISOFORM_CANONICAL VARCHAR(10000),
	IS_NEGATIVE VARCHAR(10000),
	LOCATION_BEGIN VARCHAR(10000),
	LOCATION_BEGIN_MASTER VARCHAR(10000),
	LOCATION_END VARCHAR(10000),
	LOCATION_END_MASTER VARCHAR(10000),
	NEXTPROT_ACCESSION VARCHAR(10000),
	OBJECT_ANNOTATION_IDS VARCHAR(10000),
	OBJECT_ANNOT_ENTRY_UNAMES VARCHAR(10000),
	OBJECT_ANNOT_ISO_UNAMES VARCHAR(10000),
	OBJECT_STATEMENT_IDS VARCHAR(10000),
	RAW_STATEMENT_ID VARCHAR(10000),
	REFERENCE_ACCESSION VARCHAR(10000),
	REFERENCE_DATABASE VARCHAR(10000),
	RESOURCE_TYPE VARCHAR(10000),
	SOURCE VARCHAR(10000),
	STATEMENT_ID VARCHAR(10000),
	SUBJECT_ANNOTATION_IDS VARCHAR(10000),
	SUBJECT_STATEMENT_IDS VARCHAR(10000),
	TARGET_ISOFORMS VARCHAR(10000),
	VARIANT_ORIGINAL_AMINO_ACID VARCHAR(10000),
	VARIANT_VARIATION_AMINO_ACID VARCHAR(10000));
CREATE INDEX RAW_STATEM_ENTRY_AC_IDX ON nxflat.RAW_STATEMENTS ( ENTRY_ACCESSION );
CREATE INDEX RAW_STATEM_ANNOT_ID_IDX ON nxflat.RAW_STATEMENTS ( ANNOTATION_ID );

