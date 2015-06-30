select iso.unique_name as isoform_name, p.first_pos, p.last_pos
from nextprot.sequence_identifiers ma 
inner join nextprot.mapping_annotations map on (ma.identifier_id=map.reference_identifier_id)
inner join nextprot.sequence_identifiers iso on (map.mapped_identifier_id=iso.identifier_id)
inner join nextprot.mapping_positions p on (p.annotation_id=map.annotation_id)
inner join nextprot.cv_mapping_annotation_types mat on (map.cv_type_id=mat.cv_id)
where mat.cv_name='PROTEIN_ISOFORM_MASTER_SEQUENCE'
and ma.unique_name= :entryName
order by map.rank,p.first_pos


