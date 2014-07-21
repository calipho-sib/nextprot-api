select assoc.assoc_id evidence_id, assoc_props.property_name property_name, assoc_props.property_value property_value
from nextprot.annotation_resource_assoc assoc     
inner join nextprot.annotation_resource_assoc_properties assoc_props on (assoc.assoc_id = assoc_props.annotation_resource_id)
where assoc.assoc_id in (:ids)