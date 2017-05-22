select ira.resource_id, (1000000000 +p.identifier_resource_assoc_property_id) as resource_property_id, p.property_name,p.property_value
from nextprot.sequence_identifiers si 
inner join nextprot.identifier_resource_assoc ira on (si.identifier_id=ira.identifier_id)
inner join nextprot.identifier_resource_assoc_properties p on (ira.assoc_id = p.identifier_resource_assoc_property_id)
where 
si.unique_name = :entryName
and ira.resource_id in (:resourceIds)
