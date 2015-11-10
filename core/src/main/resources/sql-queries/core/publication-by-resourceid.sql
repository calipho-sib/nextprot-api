         select pubs.*, pubtypes.cv_name as pub_type, rp.property_value submission_database, 
  (select pv.large_scale from nextprot.publication_view pv where pv.resource_id=pubs.resource_id limit 1) as is_largescale,         
  (select pv.annotated from nextprot.publication_view pv where pv.resource_id=pubs.resource_id limit 1) as is_curated,   
  (select pv.computed from nextprot.publication_view pv where pv.resource_id=pubs.resource_id limit 1) as is_computed    
  -- TODO: reassess the way we define 'curared/computed' and get rid of the 'limit 1'
 	   --nextprot.publication_view.large_scale as is_largescale,    -- PUBLI IS_LARGESCALE
           from nextprot.publications pubs
     inner join nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)
left outer join nextprot.resource_properties rp on (pubs.resource_id = rp.resource_id and rp.property_name = 'database') 
           where pubs.resource_id = :resourceId
