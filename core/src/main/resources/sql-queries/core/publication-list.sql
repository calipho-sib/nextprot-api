         select pubs.*, pubtypes.cv_name as pub_type, rp.property_value submission_database, 
  (select p.is_largescale from nextprot.view_paper_scale p where p.publication_id=pubs.resource_id) as is_largescale         
           from nextprot.publications pubs
     inner join  nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)
left outer join nextprot.resource_properties rp on (pubs.resource_id = rp.resource_id and rp.property_name = 'database') 
