select ara.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac
from nextprot.sequence_identifiers sim
inner join nextprot.annotations a on (sim.identifier_id = a.identifier_id and a.cv_annotation_type_id in (13, 1021, 1023))
inner join nextprot.annotation_resource_assoc ara on (a.annotation_id = ara.annotation_id)
inner join nextprot.publication_db_xref_assoc pxra_pubmed on (pxra_pubmed.publication_id = ara.resource_id and pxra_pubmed.cv_type_id = 1)
inner join nextprot.publication_db_xref_assoc pxra_doc on (pxra_doc.db_xref_id = pxra_pubmed.db_xref_id and pxra_doc.cv_type_id = 5)
inner join nextprot.publications mdata on (mdata.resource_id = pxra_doc.publication_id and mdata.cv_publication_type_id = 80)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_mdata.publication_id = pxra_doc.publication_id and pxra_mdata.cv_type_id = 1)
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.resource_id = pxra_mdata.db_xref_id and xr_mdata.accession like 'MDATA%')
inner join nextprot.cv_datasources ds on (ds.cv_id = ara.assigned_by_id and ds.cv_name = 'NextProt')
where sim.unique_name= :entry_name
UNION
select ara.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac
from nextprot.sequence_identifiers sim 
inner join nextprot.annotations a on (sim.identifier_id = a.identifier_id and a.cv_annotation_type_id in (13, 1021, 1023))
inner join nextprot.annotation_resource_assoc ara on (a.annotation_id = ara.annotation_id)
inner join nextprot.annotation_resource_assoc_properties arap on (arap.annotation_resource_id = ara.assoc_id and arap.property_name = 'Metadata')
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.accession = arap.property_value)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_mdata.db_xref_id = xr_mdata.resource_id and pxra_mdata.cv_type_id = 1)
inner join nextprot.publications mdata on (mdata.resource_id = pxra_mdata.publication_id and mdata.cv_publication_type_id = 80)
inner join nextprot.cv_datasources ds on (ds.cv_id = ara.assigned_by_id and ds.cv_name = 'PeptideAtlas human phosphoproteome')
where sim.unique_name = :entry_name
-- 'NX_Q99622'

