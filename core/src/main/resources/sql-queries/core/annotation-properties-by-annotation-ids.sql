select p.annotation_id, p.property_name, p.property_value, x.accession    
from nextprot.annotation_properties p    
left join nextprot.cv_terms cv on (cv.cv_id = p.cv_term_value_id)    
left join nextprot.db_xrefs x on (cv.db_xref_id = x.resource_id)    
where p.annotation_id in (:ids)
--and p.property_name != 'mutation AA'
