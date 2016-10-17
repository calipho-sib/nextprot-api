select distinct synonym_name
from nextprot.view_master_identifier_names
where is_main = true
and name_type = 'gene name'
