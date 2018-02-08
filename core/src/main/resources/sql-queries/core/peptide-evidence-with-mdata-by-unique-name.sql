select  ira.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac
from nextprot.sequence_identifiers sim 
inner join nextprot.mapping_annotations ma on (sim.identifier_id = ma.reference_identifier_id and ma.cv_type_id in (10))
inner join nextprot.sequence_identifiers si on (si.identifier_id = ma.mapped_identifier_id and si.cv_type_id = 7)
inner join nextprot.identifier_resource_assoc ira on (ira.identifier_id = si.identifier_id)
inner join nextprot.cv_datasources ds on (ds.cv_id = ira.datasource_id and ds.cv_name != 'PeptideAtlas human phosphoproteome')
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.resource_id = ds.document_id)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_mdata.db_xref_id = xr_mdata.resource_id and pxra_mdata.cv_type_id = 1)
inner join nextprot.publications mdata on (mdata.resource_id = pxra_mdata.publication_id)
where sim.unique_name = :entry_name
UNION
select ira.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac
from nextprot.sequence_identifiers sim  
inner join nextprot.mapping_annotations ma on (sim.identifier_id = ma.reference_identifier_id and ma.cv_type_id in (10))
inner join nextprot.sequence_identifiers si on (si.identifier_id = ma.mapped_identifier_id and si.cv_type_id = 7)
inner join nextprot.identifier_resource_assoc ira on (ira.identifier_id = si.identifier_id)
inner join nextprot.identifier_resource_assoc_properties irap on (irap.identifier_resource_id = ira.assoc_id and irap.property_name = 'Metadata')
inner join nextprot.cv_datasources ds on (ds.cv_id = ira.datasource_id and ds.cv_name = 'PeptideAtlas human phosphoproteome')
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.accession = irap.property_value)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_mdata.db_xref_id = xr_mdata.resource_id and pxra_mdata.cv_type_id = 1)
inner join nextprot.publications mdata on (mdata.resource_id = pxra_mdata.publication_id and mdata.cv_publication_type_id = 80)
where sim.unique_name = :entry_name
-- 'NX_Q99622'