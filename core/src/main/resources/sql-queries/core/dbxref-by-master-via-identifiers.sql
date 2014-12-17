select distinct
x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession 
from nextprot.view_master_identifier_identifiers vmm 
left join nextprot.db_xrefs x on x.accession = vmm.identifier_name
inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id
inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id 
where unique_name = :uniqueName
and db_name in ('Ensembl', 'HPA')
