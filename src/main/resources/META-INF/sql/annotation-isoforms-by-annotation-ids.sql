select assoc.annotation_id, (pos.first_pos  + 1) first_pos, pos.last_pos, seq.unique_name,
(select cv.cv_name from nextprot.cv_specificity_qualifier_types cv where cv.cv_id=assoc.cv_specificity_qualifier_type_id) as iso_specificity
from nextprot.annotation_protein_assoc assoc  
inner join nextprot.sequence_identifiers seq on (assoc.protein_id = seq.identifier_id)  
left join nextprot.protein_feature_positions pos on (pos.annotation_protein_id = assoc.assoc_id) 
where assoc.annotation_id in (:ids)
order by seq.unique_name