  select q.cv_name as type , q.description as description, 0 as n
    from nextprot.cv_qualifier_types q  
    where q.cv_name not in ('POTENTIAL', 'PROBABLE','BY_SIMILARITY', 'UNKNOWN')
    
--  select q.cv_name as type , q.description as description, count(q.cv_name) as n
--    from nextprot.annotation_resource_assoc a, 
--         nextprot.cv_qualifier_types q 
--   where a.cv_qualifier_type_id=q.cv_id 
--   group by q.cv_name, q.description
--   order by n
