select xref.resource_id, db.cv_name database_name, db.url database_url, db.link_url database_link, cat.cv_name database_category, xref.accession
from nextprot.db_xrefs xref
  inner join nextprot.cv_databases db on xref.cv_database_id = db.cv_id
  inner join nextprot.cv_database_categories cat on cat.cv_id = db.cv_category_id
where xref.accession = :accession and db.cv_name = :dbName
