-- how to retrieve xrefs of xeno interactants
select distinct si.unique_name, xr2.resource_id, db2.cv_name as database_name, db2.url as database_url, db2.link_url as database_link, dbc2.cv_name as database_category, xr2.accession
from nextprot.sequence_identifiers si
inner join nextprot.partnership_partner_assoc p1 on (si.db_xref_id=p1.db_xref_id)
inner join nextprot.partnerships inter on (inter.partnership_id=p1.partnership_id)
inner join nextprot.partnership_partner_assoc p2 on (inter.partnership_id=p2.partnership_id)
inner join nextprot.db_xrefs xr2 on (p2.db_xref_id=xr2.resource_id)
inner join nextprot.cv_databases db2 on (xr2.cv_database_id=db2.cv_id)
inner join nextprot.cv_database_categories dbc2 on (dbc2.cv_id=db2.cv_category_id) 
where p1.assoc_id!=p2.assoc_id and inter.is_xeno=true
and si.unique_name= :uniqueName

