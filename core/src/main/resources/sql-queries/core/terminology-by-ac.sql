   select distinct nextprot.db_xrefs.accession as accession, 
          nextprot.cv_terms.cv_name as name, 
          nextprot.cv_terms.description as description, 
          nextprot.cv_term_categories.cv_api_name as ontology, 
-- get ancestor          
          (select string_agg((select xref.accession from nextprot.db_xrefs   xref where parent.db_xref_id=xref.resource_id ),'|')
      from nextprot.cv_terms parent
inner join nextprot.cv_term_relationships root_r on (parent.cv_id=root_r.object_id)
inner join nextprot.cv_terms root on (root_r.subject_id=root.cv_id)    
inner join nextprot.db_xrefs xrefr on (root.db_xref_id=xrefr.resource_id) 
     where xrefr.accession=nextprot.db_xrefs.accession)  as ancestor, 
-- get sameas     
          (select string_agg((case when db.cv_category_id=7 then eq_xref.accession else null end) ,'|')
             from nextprot.db_xrefs eq_xref 
       left outer join nextprot.cv_term_db_xref_assoc eq on (nextprot.cv_terms.cv_id=eq.cv_term_id)
       left outer join nextprot.cv_databases db on (db.cv_id = eq_xref.cv_database_id)
           where eq.db_xref_id=eq_xref.resource_id and eq_xref.resource_id is not null ) as sameas
     from nextprot.cv_terms
inner join nextprot.db_xrefs on (nextprot.cv_terms.db_xref_id = nextprot.db_xrefs.resource_id)
inner join nextprot.cv_term_categories on (nextprot.cv_terms.cv_category_id = nextprot.cv_term_categories.cv_id)
     where nextprot.db_xrefs.accession=:accession and nextprot.cv_terms.cv_status_id=1
  order by nextprot.cv_term_categories.cv_api_name
  