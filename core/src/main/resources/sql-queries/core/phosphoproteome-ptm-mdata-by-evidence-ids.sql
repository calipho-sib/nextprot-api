select evi.assoc_id as evidence_id, mdata.resource_id as mdata_id, xr_mdata.accession as mdata_ac, mdata.title as mdata_title, mdata.abstract_text as mdata_xml  
from nextprot.annotation_resource_assoc evi 
inner join nextprot.annotation_resource_assoc_properties evip on (evip.annotation_resource_id = evi.assoc_id and evip.property_name = 'Metadata')
inner join nextprot.db_xrefs xr_mdata on (xr_mdata.accession = evip.property_value)
inner join nextprot.publication_db_xref_assoc pxra_mdata on (pxra_mdata.db_xref_id = xr_mdata.resource_id and pxra_mdata.cv_type_id = 1)
inner join nextprot.publications mdata on (mdata.resource_id = pxra_mdata.publication_id and mdata.cv_publication_type_id = 80)
inner join nextprot.cv_datasources ds on (ds.cv_id = evi.assigned_by_id)
where ds.cv_name = 'PeptideAtlas human phosphoproteome'
and evi.assoc_id in (:ids)
