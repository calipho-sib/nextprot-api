select distinct *
from 
(
	(
		select x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession 
		from nextprot.sequence_identifiers si
		inner join nextprot.identifier_resource_assoc assoc on si.identifier_id = assoc.identifier_id 
		inner join nextprot.resources res on res.resource_id = assoc.resource_id 
		inner join nextprot.db_xrefs x on res.resource_id = x.resource_id 
		inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id  
		inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id 
		where res.cv_type_id = 1 
		and si.unique_name = :uniqueName
		and dbs.cv_name not in ('Ensembl', 'HPA')
	)
	union
	(
		select x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession 
		from nextprot.view_master_identifier_identifiers vmm 
		left join nextprot.db_xrefs x on x.accession = vmm.identifier_name
		inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id
		inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id 
		where unique_name = :uniqueName
		and db_name in ('Ensembl', 'HPA')
	)
	union
	(
		select x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession 
		from nextprot.sequence_identifiers si
		inner join nextprot.annotations ann on ann.identifier_id = si.identifier_id
		inner join nextprot.annotation_resource_assoc ares on ares.annotation_id = ann.annotation_id
		inner join nextprot.resources res on res.resource_id = ares.resource_id
		inner join nextprot.db_xrefs x on res.resource_id = x.resource_id 
		inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id  
		inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id 
		where si.unique_name = :uniqueName
		and si.cv_type_id = 1		
	) 
) tir
order by database_name