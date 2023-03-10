select distinct 
peptide.unique_name, xr.accession, db.cv_name as database_name, ds.cv_name as assigned_by, 
ira.resource_id as resource_id, rt.cv_name as resource_type,
ira.assoc_id as evidence_id
from nextprot.sequence_identifiers peptide
inner join nextprot.identifier_resource_assoc ira on (peptide.identifier_id=ira.identifier_id)
inner join nextprot.resources res on (ira.resource_id=res.resource_id)
inner join nextprot.cv_resource_types rt on (res.cv_type_id = rt.cv_id)  
inner join nextprot.db_xrefs xr on (ira.resource_id=xr.resource_id)
inner join nextprot.cv_databases db on (xr.cv_database_id=db.cv_id)
inner join nextprot.cv_datasources ds on (ira.datasource_id=ds.cv_id)
where ds.cv_name != 'PeptideAtlas' and ds.cv_name != 'MassIVE' and :datasourceClause 
and peptide.unique_name in (:names)
