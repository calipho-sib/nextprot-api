select xr.accession as accession, null::text as family_region, cv.cv_name as family_name, null::text as description, cv.cv_id as family_id
from nextprot.cv_term_relationships rel
inner join nextprot.cv_terms cv on (rel.object_id=cv.cv_id)
inner join nextprot.db_xrefs xr on (cv.db_xref_id = xr.resource_id)
where rel.subject_id= :familyId;
