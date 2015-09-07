select CASE WHEN name_type='CD antigen' THEN 'CD antigen'
			WHEN name_type='allergen' THEN 'allergen'
			WHEN name_type='enzyme name' THEN 'EC'
            WHEN name_type='International Nonproprietary Names' THEN 'INN'
            WHEN name_type='name' THEN 'protein'
            WHEN name_type='open reading frame' THEN 'ORF'
            ELSE name_type END as category, 
        unique_name, name_class, name_type,
        CASE WHEN name_type='CD antigen' THEN 'CD antigen'   
  			 WHEN name_type='allergen' THEN 'allergen'
	         WHEN name_type='enzyme name' THEN 'EC'  
  			 WHEN name_type='International Nonproprietary Names' THEN 'INN'
  			 ELSE name_qualifier END as name_qualifier, 
         is_main, 
         CASE  WHEN name_type='enzyme name' THEN substr(synonym_name,4)     -- removes the 'EC ' when its an enzyme
         ELSE synonym_name END as synonym_name, synonym_id, parent_id
from nextprot.view_master_identifier_names
where unique_name = :uniqueName 
order by unique_name