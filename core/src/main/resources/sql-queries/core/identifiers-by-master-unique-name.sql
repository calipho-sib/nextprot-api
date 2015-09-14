select * 
from nextprot.view_master_identifier_identifiers vmii 
where identifier_name !~ '^(NX_)?VG_.+' and
vmii.unique_name = :uniqueName
order by vmii.type
