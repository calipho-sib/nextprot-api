select 
v.publication_num as references_curated_publications_count, 
v.additional_num as references_additional_publications_count, 
v.patent_num as references_patents_count, 
v.submission_num as references_submissions_count, 
v.online_num as references_web_resources_count, 
v.ref_num as references_count,
(select ip.property_value from nextprot.identifier_properties ip 
 where ip.cv_property_name_id=45 and ip.identifier_id=v.identifier_id ) as pe_info
from nextprot.view_master_reference_count v
where unique_name = :uniqueName
