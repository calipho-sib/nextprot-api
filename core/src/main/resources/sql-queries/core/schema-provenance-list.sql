--
-- union of 
-- database names declared in the table cv_databases 
-- and
-- identifier types declared in the view_master_identifier_identifiers
--
select 
  ds.cv_name as name, 
  ds.description, 
  ds.url, 
  cat.cv_name as category 
from nextprot.cv_databases ds
inner join nextprot.cv_database_categories cat on (ds.cv_category_id=cat.cv_id)
union
select name, 'Identifier subtype'::varchar as description, null::varchar as url, 'Identifier subset'::varchar as category from (
select coalesce(a.db_name, a.type) as name,(select cv_id from nextprot.cv_databases db where db.cv_Name = a.db_name) as hasDb from (
select type, db_name, count(*) from nextprot.view_master_identifier_identifiers
group by type, db_name
) a ) b where hasDb is null and name not in ('PIR','IPI', 'HGNC')
