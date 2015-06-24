  select pubs.resource_id pub_id 
           from nextprot.publications pubs
inner join  nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)