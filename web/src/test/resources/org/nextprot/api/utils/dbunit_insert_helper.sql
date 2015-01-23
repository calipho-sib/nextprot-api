-- This file is used to help create dbunit tests based on real examples find in the database

select '<identifier_synonyms'  || ' identifier_id="' || syn.identifier_id || '" synonym_name="' || syn.synonym_name || '" cv_type_id="' || syn.cv_type_id || '" synonym_id="' || syn.synonym_id || '" />'
select '<sequence_identifiers'  || ' unique_name="' || transcript.unique_name || '" identifier_id="' || transcript.identifier_id || '" cv_type_id="' || transcript.cv_type_id || '" db_xref_id="' || transcript.db_xref_id || '" />'
select '<mapping_annotations annotation_id="' || exon_mapping.annotation_id || '" reference_identifier_id="' || exon_mapping.reference_identifier_id || '" mapped_identifier_id="' || exon_mapping.mapped_identifier_id || '" cv_type_id="' || exon_mapping.cv_type_id || '" rank="' || exon_mapping.rank || '" />'
select '<bio_sequences bio_sequence_id="' || bio_seq.bio_sequence_id || '" identifier_id="' || bio_seq.identifier_id || '" cv_type_id="' || bio_seq.cv_type_id || '" bio_sequence="' || bio_seq.bio_sequence || '" />'
select '<identifier_resource_assoc identifier_id="' || assoc_ens_protein.identifier_id || '" resource_id="' || assoc_ens_protein.resource_id || '" datasource_id="' || assoc_ens_protein.datasource_id || '" cv_type_id="' || assoc_ens_protein.cv_type_id || '" />'
select '<db_xrefs resource_id="' || xrefs.resource_id || '" accession="' || xrefs.accession || '" cv_database_id="' || xrefs.cv_database_id || '" />'
select '<resources resource_id="' || ens_protein.resource_id || '" cv_type_id="' || ens_protein.cv_type_id ||'" />'
select '<cv_databases'  || ' cv_id="' || dbs.cv_id || '" cv_name="' || dbs.cv_name || '" url="' || dbs.url || '" link_url="' || dbs.link_url || '" />'
select distinct '<mapping_annotations annotation_id="' || mapping.annotation_id || '" reference_identifier_id="' || mapping.reference_identifier_id || '" mapped_identifier_id="' || mapping.mapped_identifier_id || '" cv_type_id="' || mapping.cv_type_id || '" cv_quality_qualifier_id="' || coalesce(mapping_gene.cv_quality_qualifier_id,0) || '" />'
select distinct '<mapping_positions position_id="' || positions.position_id || '" first_pos="' || positions.first_pos || '" last_pos="' || positions.last_pos || '" annotation_id="' || positions.annotation_id || '" />'
