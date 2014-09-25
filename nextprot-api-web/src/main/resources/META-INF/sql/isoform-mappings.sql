select isoform.unique_name isoform, bio_sequences.bio_sequence bio_sequence, (positions.first_pos + 1) first_position , positions.last_pos last_position, mapping.reference_identifier_id, gene.unique_name reference_gene   
				from nextprot.sequence_identifiers isoforms    
				inner join nextprot.mapping_annotations mapping on (mapping.mapped_identifier_id = isoforms.identifier_id)  
				inner join nextprot.cv_mapping_annotation_types mapping_types on (mapping.cv_type_id = mapping_types.cv_id)  
				inner join nextprot.mapping_positions positions on (mapping.annotation_id = positions.annotation_id)  
				inner join nextprot.sequence_identifiers isoform on (isoform.identifier_id = mapping.mapped_identifier_id)  
				inner join nextprot.bio_sequences bio_sequences on (isoform.identifier_id = bio_sequences.identifier_id)  
				inner join nextprot.sequence_identifiers gene on (gene.identifier_id = mapping.reference_identifier_id)  
				where  mapping_types.cv_name = 'PROTEIN_ISOFORM_GENE'  
				and isoform.unique_name in (:isoform_names)  
				order by gene.unique_name, isoform.unique_name, positions.first_pos, positions.last_pos
				