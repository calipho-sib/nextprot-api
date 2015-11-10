select p1.* as id, pubtypes.cv_name as pub_type, rp.property_value as submission_database , 
  (select pv.large_scale from nextprot.publication_view pv where pv.resource_id=p1.resource_id limit 1) as is_largescale,         
  (select pv.annotated from nextprot.publication_view pv where pv.resource_id=p1.resource_id limit 1) as is_curated,   
  (select pv.computed from nextprot.publication_view pv where pv.resource_id=p1.resource_id limit 1) as is_computed    
            from nextprot.publications p1 
                 inner join nextprot.cv_publication_types as pubtypes on p1.cv_publication_type_id = pubtypes.cv_id 
                 left outer join nextprot.cv_journals as j on (p1.cv_journal_id = j.cv_id) 
                 left outer join nextprot.resource_properties rp on (p1.resource_id = rp.resource_id and rp.property_name = 'database')
                 inner join (
                  select p.resource_id as id 
                   from nextprot.identifier_resource_assoc ira, 
                        nextprot.publications p,
                        nextprot.cv_datasources ds
                  where ira.identifier_id = :identifierId   
                    and ira.resource_id = p.resource_id 
                    and ira.datasource_id = ds.cv_id 
                    --and ds.cv_name != 'PIR'
                    and p.cv_publication_type_id in (:publicationTypes) 
                  union 
                 select p.resource_id as id 
                   from nextprot.annotation_resource_assoc ara, nextprot.annotations a, 
                        nextprot.publications p 
                  where ara.resource_id = p.resource_id
                    and ara.annotation_id = a.annotation_id 
                    and a.identifier_id = :identifierId
                    and p.cv_publication_type_id in (:publicationTypes)  
                  ) as pub_r on (p1.resource_id = pub_r.id) 
          order by extract (year from p1.publication_date) desc,  
            p1.cv_publication_type_id,  
            j.journal_name asc,  
            p1.volume asc,  
            p1.first_page asc
            