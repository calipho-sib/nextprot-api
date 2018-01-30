select pep.unique_name pep_unique_name, string_agg(distinct iso.unique_name,',') as iso_names
from nextprot.sequence_identifiers iso
inner join nextprot.mapping_annotations map on (Iso.identifier_id=map.reference_identifier_id)
inner join nextprot.sequence_identifiers pep on (map.mapped_identifier_id=pep.identifier_id) 
where iso.cv_type_id=2 and iso.cv_status_id=1
and pep.cv_type_id=7 and pep.cv_status_id=1
group by pep.unique_name