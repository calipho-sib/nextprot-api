select 
db.cv_id as db_id, db.cv_name as db_name, db.url as db_url,
cat.cv_id as cat_id, cat.cv_name as cat_name 
from nextprot.cv_databases db 
inner join nextprot.cv_database_categories cat on (db.cv_category_id=cat.cv_id)