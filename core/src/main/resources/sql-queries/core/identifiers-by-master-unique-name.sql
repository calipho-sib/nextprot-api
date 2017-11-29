select vmii.*, db.link_url as link_template
from nextprot.view_master_identifier_identifiers vmii 
left outer join nextprot.cv_databases db on (db.cv_name=vmii.db_name)
--where identifier_name !~ '^(NX_)?VG_.+'
where identifier_name ~ '^ENS.+' -- see CALIPHOMISC-489
and vmii.unique_name = :uniqueName
order by vmii.type, vmii.identifier_name
