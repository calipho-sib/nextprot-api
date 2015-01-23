select * 
from nextprot.view_master_identifier_identifiers vmii 
where identifier_name not like 'NX_VG%' and 
vmii.unique_name = :uniqueName
order by vmii.type
