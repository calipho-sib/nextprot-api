select vmpa.pub_id, irap.property_name, irap.property_value
from nextprot.view_master_publication_assoc vmpa
inner join nextprot.identifier_resource_assoc ira on (vmpa.entry_id=ira.identifier_id and vmpa.pub_id=ira.resource_id)
inner join nextprot.identifier_resource_assoc_properties irap on (ira.assoc_id=irap.identifier_resource_id)
where vmpa.entry_id=:masterId

