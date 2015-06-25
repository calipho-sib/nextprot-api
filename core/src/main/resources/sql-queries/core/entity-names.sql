select * 
from nextprot.view_master_identifier_names
where unique_name = :uniqueName 
order by unique_name
	