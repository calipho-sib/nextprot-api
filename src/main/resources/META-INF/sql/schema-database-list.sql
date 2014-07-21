select ds.cv_name as name, ds.description, ds.url, cat.cv_name as category from nextprot.cv_databases ds
inner join nextprot.cv_database_categories cat on (ds.cv_category_id=cat.cv_id);
