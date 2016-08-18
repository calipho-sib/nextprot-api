select pubs.resource_id 
from nextprot.publications pubs
inner join nextprot.publication_db_xref_assoc assoc on assoc.publication_id = pubs.resource_id
inner join nextprot.db_xrefs x on assoc.db_xref_id = x.resource_id
inner join nextprot.cv_databases dbs on x.cv_database_id = dbs.cv_id 
where x.accession = :accession
and dbs.cv_name = :database