select distinct unique_name
from nextprot.view_master_identifier_names
where lower(synonym_name) = lower(:geneName)
and is_main = true
and name_type = 'gene name'