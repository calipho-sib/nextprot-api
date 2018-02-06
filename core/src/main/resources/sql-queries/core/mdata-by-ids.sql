select p.resource_id as mdata_id, x.accession as mdata_ac, p.title as mdata_title, p.abstract_text as mdata_xml
from nextprot.publications p 
inner join nextprot.publication_db_xref_assoc pxa on (p.resource_id=pxa.publication_id and pxa.cv_type_id=1)
inner join nextprot.db_xrefs x on (pxa.db_xref_id = x.resource_id)
where p.resource_id in ( :mdata_ids )