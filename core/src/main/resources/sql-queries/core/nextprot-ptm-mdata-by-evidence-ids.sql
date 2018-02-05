select evi.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac, mdata.title as mdata_title, mdata.abstract_text as mdata_xml
from nextprot.annotation_resource_assoc evi
inner join nextprot.publications p on (p.resource_id=evi.resource_id)
inner join nextprot.publication_db_xref_assoc pxra on (evi.resource_id=pxra.publication_id and pxra.cv_type_id=1)
inner join nextprot.publication_db_xref_assoc pxra_doc on (pxra.db_xref_id=pxra_doc.db_xref_id and pxra_doc.cv_type_id=5)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_doc.publication_id=pxra_mdata.publication_id and pxra_mdata.cv_type_id=1)
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.resource_id=pxra_mdata.db_xref_id and xr_mdata.accession like 'MDATA%')
inner join nextprot.publications mdata on (pxra_doc.publication_id=mdata.resource_id)
where evi.assoc_id in (:ids)
