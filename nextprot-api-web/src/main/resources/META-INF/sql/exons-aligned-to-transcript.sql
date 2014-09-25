select gene.unique_name gene_name, transcript.unique_name transcript_name, exons.unique_name exon, (positions.first_pos + 1) first_position, positions.last_pos last_position, (mapping.rank - 1) rank
from nextprot.sequence_identifiers exons  
inner join nextprot.mapping_annotations mapping on (mapping.mapped_identifier_id = exons.identifier_id)  
inner join nextprot.cv_mapping_annotation_types mapping_types on (mapping.cv_type_id = mapping_types.cv_id)  
inner join nextprot.sequence_identifiers transcript on (transcript.identifier_id = mapping.reference_identifier_id)  
inner join nextprot.bio_sequences bio_seq on (bio_seq.identifier_id = exons.identifier_id)  
inner join nextprot.mapping_annotations exon_mapping on (exon_mapping.mapped_identifier_id = exons.identifier_id)  
inner join nextprot.mapping_annotations transcript_mapping on (transcript_mapping.mapped_identifier_id = transcript.identifier_id)  
inner join nextprot.sequence_identifiers gene on (gene.identifier_id = exon_mapping.reference_identifier_id)  
left join nextprot.mapping_positions positions on (exon_mapping.annotation_id  = positions.annotation_id)  
where mapping_types.cv_name = 'EXON_TRANSCRIPT'  
and exon_mapping.cv_type_id = 1   
and transcript.unique_name = :transcriptName
and gene.unique_name = :geneName
order by gene_name, transcript_name, first_position