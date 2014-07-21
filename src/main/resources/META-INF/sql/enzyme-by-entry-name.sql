select  t2.cv_name as name,  x.accession as accession
         from nextprot.annotations a 
         inner join nextprot.sequence_identifiers s on (s.identifier_id = a.identifier_id)  
         left join nextprot.cv_terms t on (a.cv_annotation_type_id = t.cv_id)  
         left join nextprot.cv_terms t2 on (a.cv_term_id = t2.cv_id) 
         left join nextprot.db_xrefs x on (t2.db_xref_id = x.resource_id)  
         where t.cv_name ='enzyme classification' 
         and s.unique_name = :uniqueName        
