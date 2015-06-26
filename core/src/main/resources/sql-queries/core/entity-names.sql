select CASE WHEN name_type='enzyme name' THEN 'EC'
            WHEN name_type='International Nonproprietary Names' THEN 'INN'
            ELSE name_type END as category, 
            unique_name, name_class, name_type, name_qualifier, is_main, synonym_name, synonym_id, parent_id
from nextprot.view_master_identifier_names
where unique_name = :uniqueName 
order by unique_name