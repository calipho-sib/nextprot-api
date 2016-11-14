select v.* 
from nextprot.isoform_identifier_view v
inner join nextprot.sequence_identifiers mas on (v.master_id=mas.identifier_id)
inner join nextprot.sequence_identifiers iso on (v.isoform_id=iso.identifier_id)
where mas.cv_status_id=1 and mas.cv_type_id=1
and   iso.cv_status_id=1 and iso.cv_type_id=2
