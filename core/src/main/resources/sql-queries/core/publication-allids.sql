-- now we only take into account publications related to AT LEAST ONE valid entry by an annotation evidence, a mapping evidence or a direct link 
-- new view
select distinct pub_id from nextprot.view_master_publication_assoc
-- old view
-- select distinct resource_id as pub_id from nextprot.publication_view pv
-- where pv.annotated > 0 or pv.large_scale > 0 or pv.nucleotide_seq > 0 or pv.computed > 0 or pv.annotation_num > 0 or pv.identifier_num > 0
