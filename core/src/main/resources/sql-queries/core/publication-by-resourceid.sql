select 
  pubs.*, pubtypes.cv_name as pub_type, rp.property_value submission_database, 
  rp2.property_value as journal_from_property,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=pubs.resource_id and rp2.property_name='publisher') as publisher,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=pubs.resource_id and rp2.property_name='city') as city,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=pubs.resource_id and rp2.property_name='comment') as title_for_web_resource
from nextprot.publications pubs
inner join nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)
left outer join nextprot.resource_properties rp on (pubs.resource_id = rp.resource_id and rp.property_name = 'database') 
left outer join nextprot.resource_properties rp2 on (pubs.resource_id = rp2.resource_id and rp2.property_name = 'journal') 
where pubs.resource_id = :resourceId
