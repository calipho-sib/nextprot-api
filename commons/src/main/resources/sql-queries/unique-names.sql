select si.unique_name
from nextprot.sequence_identifiers si
where si.cv_type_id = 1
and si.cv_status_id = 1
order by si.unique_name; 