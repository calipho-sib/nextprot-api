select distinct
px.resource_id, db.cv_name as database_name, db.url as database_url, db.link_url as database_link, dbc.cv_name as database_category, px.accession 
from nextprot.sequence_identifiers si
inner join nextprot.db_xrefs six on (si.db_xref_id=six.resource_id)
inner join nextprot.partnership_partner_assoc ppa on (six.resource_id=ppa.db_xref_id)
inner join nextprot.partnerships p on (ppa.partnership_id=p.partnership_id)
inner join nextprot.partnership_resource_assoc evi on (p.partnership_id=evi.partnership_id)
inner join nextprot.db_xrefs px on (evi.resource_id=px.resource_id)
inner join nextprot.cv_databases db on (px.cv_database_id=db.cv_id)
inner join nextprot.cv_database_categories dbc on (dbc.cv_id=db.cv_category_id) 
where si.unique_name like :uniqueName || '%'

