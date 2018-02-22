select evi.assoc_id as evidence_id,  si.unique_name, a.annotation_id as annot_id, t.cv_name as annot_type, t2.cv_name as annot_term, 
src.cv_id as src_id, src.cv_name as src_name, iso.unique_Name as iso, pfp.first_pos + 1 as first_pos
from nextprot.sequence_identifiers si
inner join nextprot.annotations a on (si.identifier_id=a.identifier_id)
inner join nextprot.annotation_resource_assoc evi on (a.annotation_id=evi.annotation_id)
inner join nextprot.annotation_protein_assoc apa on (a.annotation_id=apa.annotation_id)
inner join nextprot.sequence_identifiers iso on (apa.protein_id=iso.identifier_id)
inner join nextprot.protein_feature_positions pfp on (pfp.annotation_protein_id=apa.assoc_id)
inner join nextprot.cv_terms t on (a.cv_annotation_type_id=t.cv_id)
inner join nextprot.cv_terms t2 on (a.cv_term_id=t2.cv_id)
inner join nextprot.cv_datasources src on (evi.assigned_by_id=src.cv_id)
where si.cv_type_id=1 and si.cv_status_id=1
and a.cv_annotation_type_id in (13,1021,1023)
and src.cv_name='NextProt'
and iso.unique_name ilike '%-1'
limit :sample_size