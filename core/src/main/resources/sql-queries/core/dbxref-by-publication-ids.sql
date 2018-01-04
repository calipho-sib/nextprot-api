select x.resource_id, dbs.cv_name database_name, dbs.url database_url, dbs.link_url database_link, cat.cv_name database_category, x.accession, assoc.publication_id
from nextprot.db_xrefs x
	inner join nextprot.publication_db_xref_assoc assoc on assoc.db_xref_id = x.resource_id
	inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id
	inner join nextprot.cv_database_categories cat on cat.cv_id = dbs.cv_category_id
where assoc.publication_id in (:publicationIds)
ORDER BY database_name