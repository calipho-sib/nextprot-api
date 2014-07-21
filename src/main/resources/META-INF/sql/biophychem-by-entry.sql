select distinct cv.display_name, ip.property_value
from nextprot.identifier_properties ip 
inner join nextprot.cv_property_names cv on ip.cv_property_name_id = cv.cv_id 
inner join nextprot.sequence_identifiers si on ip.identifier_id = si.identifier_id 
and cv.cv_id between 16 and 23
and si.unique_name = :uniqueName