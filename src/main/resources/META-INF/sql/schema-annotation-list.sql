select root.cv_name as parent, first.cv_name as name , first.description as description/*, first.cv_id as rid, root.cv_id as pid */  
  from nextprot.cv_terms first 
 inner join nextprot.cv_term_relationships root_r on (first.cv_id=root_r.subject_id) 
 inner join nextprot.cv_terms root on (root_r.object_id=root.cv_id) 
 where root.cv_id in (2,3,4,10,11,12,13,14,15,16,17)
 order by first.cv_id