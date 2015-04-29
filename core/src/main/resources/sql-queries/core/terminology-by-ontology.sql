   select distinct
          nextprot.cv_terms.cv_id as id, 
          nextprot.db_xrefs.accession as accession, 
          nextprot.cv_terms.cv_name as name, 
          nextprot.cv_terms.description as description, 
          nextprot.cv_term_categories.cv_api_name as ontology, 
          (select string_agg(cvsyn.synonym_name, ' | ') from  nextprot.cv_term_synonyms cvsyn where cvsyn.cv_term_id=nextprot.cv_terms.cv_id ) as synonyms,
          (select string_agg(properties.property_name ||':='|| properties.property_value, ' | ') from nextprot.cv_term_properties properties where properties.cv_term_id = nextprot.cv_terms.cv_id) as properties,
-- get ancestor          
          (select string_agg((select xref.accession from nextprot.db_xrefs   xref where parent.db_xref_id=xref.resource_id ),'|')
      from nextprot.cv_terms parent
inner join nextprot.cv_term_relationships root_r on (parent.cv_id=root_r.object_id)
inner join nextprot.cv_terms root on (root_r.subject_id=root.cv_id)    
inner join nextprot.db_xrefs xrefr on (root.db_xref_id=xrefr.resource_id) 
     where xrefr.accession=nextprot.db_xrefs.accession)  as ancestor, 
-- get other_xrefs
   (select string_agg(cat.cv_name || ', ' || db.cv_name || ', ' || ref.accession  || ', ' || db.link_url, ' | ') 
     from nextprot.cv_term_db_xref_assoc tra 
     inner join nextprot.db_xrefs ref on (tra.db_xref_id=ref.resource_id) 
     inner join nextprot.cv_databases db on (ref.cv_database_id=db.cv_id)
     inner join nextprot.cv_database_categories cat on (cat.cv_id = db.cv_category_id)  
     where tra.cv_term_id=nextprot.cv_terms.cv_id) as xref
     from nextprot.cv_terms
inner join nextprot.db_xrefs on (nextprot.cv_terms.db_xref_id = nextprot.db_xrefs.resource_id)
inner join nextprot.cv_term_categories on (nextprot.cv_terms.cv_category_id = nextprot.cv_term_categories.cv_id)
where nextprot.cv_term_categories.cv_api_name = :ontology 
and nextprot.cv_terms.cv_status_id=1 -- We don't take obsolete terms
order by nextprot.cv_term_categories.cv_api_name
