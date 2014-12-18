SELECT o.cv_api_name as ontology, o.description as description, o.cv_display_name as name 
  FROM nextprot.cv_term_categories o ORDER BY o.cv_api_name
