SELECT o.cv_api_name as ontology, o.description as description, o.cv_display_name as name 
FROM nextprot.cv_term_categories o 
WHERE o.cv_api_name not in ('UniprotFamilyCv') 
ORDER BY o.cv_api_name

