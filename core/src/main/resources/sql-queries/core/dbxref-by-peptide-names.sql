select distinct 
xr.resource_id, db.cv_name as database_name, db.url as database_url, db.link_url as database_link, dbc.cv_name as database_category, xr.accession 
from nextprot.sequence_identifiers peptide
inner join nextprot.identifier_resource_assoc ira on (peptide.identifier_id=ira.identifier_id)
inner join nextprot.db_xrefs xr on (ira.resource_id=xr.resource_id) 
inner join nextprot.cv_databases db on (xr.cv_database_id=db.cv_id)
inner join nextprot.cv_datasources ds on (ira.datasource_id=ds.cv_id and ds.cv_name != 'PeptideAtlas')
inner join nextprot.cv_database_categories dbc on (dbc.cv_id=db.cv_category_id) 
where peptide.unique_name in (:names)
