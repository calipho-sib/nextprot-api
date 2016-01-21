select 
  pubs.*, pubtypes.cv_name as pub_type, rp.property_value submission_database, 
  rp2.property_value as journal_from_property,
  (select pv.large_scale from nextprot.publication_view pv where pv.resource_id=pubs.resource_id) as is_largescale,         
  (select pv.annotated from nextprot.publication_view pv where pv.resource_id=pubs.resource_id) as is_curated,   
  (select (case when pv.annotated = 1 then 0 else pv.computed end) from nextprot.publication_view pv where pv.resource_id=pubs.resource_id) as is_computed    
from nextprot.publications pubs
inner join nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)
left outer join nextprot.resource_properties rp on (pubs.resource_id = rp.resource_id and rp.property_name = 'database') 
left outer join nextprot.resource_properties rp2 on (pubs.resource_id = rp2.resource_id and rp2.property_name = 'journal') 
where pubs.resource_id = :resourceId
