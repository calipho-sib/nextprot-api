select xr.accession as accession, ap.property_value as family_region, cv.cv_name as family_name, a.description as description, cv.cv_id as family_id
from nextprot.sequence_identifiers m 
inner join nextprot.annotations a on (m.identifier_id = a.identifier_id and a.cv_annotation_type_id = 1059)
inner join nextprot.cv_terms cv on (a.cv_term_id = cv.cv_id)
inner join nextprot.db_xrefs xr on (cv.db_xref_id = xr.resource_id)
left outer join nextprot.annotation_properties ap on (a.annotation_id = ap.annotation_id and ap.property_name = 'family region')
where m.cv_type_id = 1
and m.cv_status_id = 1 
and cv.cv_status_id = 1
and m.unique_name = :uniqueName
