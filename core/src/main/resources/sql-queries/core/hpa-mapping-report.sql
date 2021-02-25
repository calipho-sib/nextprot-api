select si.unique_name as nx_ac, x.accession as hpa_ac 
from nextprot.sequence_identifiers si
inner join nextprot.identifier_resource_assoc ira on (si.identifier_id=ira.identifier_id)
inner join nextprot.db_xrefs x on (x.resource_id=ira.resource_id)
where si.cv_type_id=1 and si.cv_status_id=1 and x.cv_database_id=60
order by si.unique_name, x.accession
