select isoforms.unique_name unique_name, syn.synonym_id, syn.synonym_name, syn_types.cv_name syn_type, syn_qualifiers.cv_name syn_qualifier, seqs.md5, seqs.bio_sequence, 'NX_'||props.property_value=isoforms.unique_name is_swissprot_display 
				 from nextprot.bio_sequences seqs 
				 inner join nextprot.sequence_identifiers isoforms on (seqs.identifier_id = isoforms.identifier_id) 
				 inner join nextprot.mapping_annotations mapping on (mapping.mapped_identifier_id = isoforms.identifier_id) 
				 inner join nextprot.cv_mapping_annotation_types mapping_types on (mapping.cv_type_id = mapping_types.cv_id) 
				 inner join nextprot.sequence_identifiers master on (mapping.reference_identifier_id = master.identifier_id) 
				 inner join nextprot.identifier_synonyms syn on (seqs.identifier_id = syn.identifier_id) 
				 inner join nextprot.cv_synonym_types syn_types on (syn.cv_type_id = syn_types.cv_id) 
				 left join nextprot.cv_synonym_qualifiers syn_qualifiers on (syn.cv_qualifier_id = syn_qualifiers.cv_id) 
				 inner join nextprot.identifier_properties props on (master.identifier_id = props.identifier_id) 
				 inner join nextprot.cv_property_names cv_props_name on (props.cv_property_name_id = cv_props_name.cv_id) 
				 where syn.is_main = true 
				 and mapping_types.cv_name = 'PROTEIN_ISOFORM_MASTER_SEQUENCE' 
				 and syn.cv_type_id = 1 
				 and cv_props_name.cv_name = 'swissprot displayed isoform' 
				 and master.unique_name = :unique_name;
